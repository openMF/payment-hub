/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.mojaloop.routebuilder.switchconsumer;

import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.openmf.psp.config.HubSettings;
import org.openmf.psp.config.SwitchSettings;
import org.openmf.psp.config.TenantProperties;
import org.openmf.psp.dto.PartyIdInfo;
import org.openmf.psp.internal.FspId;
import org.openmf.psp.mojaloop.cache.TransactionContextHolder;
import org.openmf.psp.mojaloop.component.IlpBuilderDpc;
import org.openmf.psp.mojaloop.constant.ExchangeHeader;
import org.openmf.psp.mojaloop.dto.mojaloop.ParticipantSwitchResponseDTO;
import org.openmf.psp.mojaloop.dto.mojaloop.PartySwitchResponseDTO;
import org.openmf.psp.mojaloop.dto.mojaloop.QuoteSwitchResponseDTO;
import org.openmf.psp.mojaloop.dto.mojaloop.TransactionRequestSwitchResponseDTO;
import org.openmf.psp.mojaloop.dto.mojaloop.TransferSwitchResponseDTO;
import org.openmf.psp.mojaloop.internal.Ilp;
import org.openmf.psp.mojaloop.internal.TransactionCacheContext;
import org.openmf.psp.mojaloop.internal.TransactionRoleContext;
import org.openmf.psp.mojaloop.type.TransactionAction;
import org.openmf.psp.mojaloop.type.TransferState;
import org.openmf.psp.type.TransactionRole;
import org.openmf.psp.util.ContextUtil;
import org.openmf.psp.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import static org.openmf.psp.type.TransactionRole.PAYEE;
import static org.openmf.psp.type.TransactionRole.PAYER;

import javax.servlet.http.HttpServletRequest;

/**
 * List of asynchronous PUT callback endpoints as response to GET, POST requests.
 * @see SwitchErrorResponseRouteBuilder
 */
@Configuration
public class SwitchResponseRouteBuilder extends RouteBuilder {

    private static Logger logger = LoggerFactory.getLogger(SwitchResponseRouteBuilder.class);

    private HubSettings hubSettings;
    private SwitchSettings switchSettings;

    private TransactionContextHolder transactionContextHolder;

    private IlpBuilderDpc ilpBuilderDpc;

    @Autowired
    public SwitchResponseRouteBuilder(CamelContext camelContext, HubSettings hubSettings, SwitchSettings switchSettings,
                                      TransactionContextHolder transactionContextHolder, IlpBuilderDpc ilpBuilderDpc) {
        super(camelContext);
        this.hubSettings = hubSettings;
        this.switchSettings = switchSettings;
        this.transactionContextHolder = transactionContextHolder;
        this.ilpBuilderDpc = ilpBuilderDpc;
    }

    @Override
    public void configure() throws Exception {
        getContext().getShutdownStrategy().setTimeout(1);

        hubSettings.getTenants().forEach(tenant -> {
            buildPartiesRoute(tenant);
            buildTransactionRequestRoute(tenant);
            buildQuotesRoute(tenant);
            buildTransferRoute(tenant);
            buildParticipantRoute(tenant);
        });


        from("direct:putPartiesResponse")
                .id("putPartiesResponse")
                .process(exchange -> {
                    Message in = exchange.getIn();
                    PartySwitchResponseDTO responseDTO = in.getBody(PartySwitchResponseDTO.class);
                    logger.debug(String.format("Incoming parties switch response: %s", JsonUtil.toJson(responseDTO)));

                    transactionContextHolder.updateSwitchParty(responseDTO);

                    PartyIdInfo idInfo = responseDTO.getParty().getPartyIdInfo();
                    exchange.setProperty(ExchangeHeader.PARTIES_INFO.getKey(), idInfo);
                })
                .to("seda:partiesResponseQueue?waitForTaskToComplete=Never")
        ;

        from("seda:partiesResponseQueue")
                .id("partiesResponseQueue")
                .loopDoWhile(exchange -> {
                    PartyIdInfo idInfo = exchange.getProperty(ExchangeHeader.PARTIES_INFO.getKey(), PartyIdInfo.class);
                    String transaction = transactionContextHolder.popWaitingPartyTransaction(idInfo);
                    if (transaction == null)
                        return false;

                    FspId currentFsp = exchange.getProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), FspId.class);
                    TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transaction);
                    TransactionRoleContext currentContext = transactionContext.getRoleContext(currentFsp);
                    TransactionRole currentRole = currentContext.getRole();

                    transactionContext.populateExchangeContext(currentRole, exchange);
                    return true;
                })
                .copy()
                    .to("seda:partiesResponseSplitQueue?waitForTaskToComplete=Never")
                .end()
                .process(exchange -> exchange.setProperty(ExchangeHeader.PARTIES_INFO.getKey(), null));
        ;

        from("seda:partiesResponseSplitQueue")
                .id("partiesResponseSplitQueue")
                .to("direct:getSwitchTransactionRequest")
        ;

        from("direct:putTransactionRequestResponse")
                .id("putTransactionRequestResponse")
                .process(exchange -> {
                    Message in = exchange.getIn();
                    TransactionRequestSwitchResponseDTO responseDTO = in.getBody(TransactionRequestSwitchResponseDTO.class);
                    logger.debug(String.format("Incoming transaction request response: %s", JsonUtil.toJson(responseDTO)));

                    String transactionId = responseDTO.getTransactionId();
                    transactionContextHolder.updateSwitchTransactionRequest(transactionId, PAYER, responseDTO); // role who created the response

                    TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);
                    transactionContext.populateExchangeContext(PAYEE, exchange); // current role
                })
                .to("seda:transactionRequestResponseQueue?waitForTaskToComplete=Never")
        ;
        from("seda:transactionRequestResponseQueue")
                .id("transactionRequestResponseQueue")
                .choice()
                .when(exchange -> {
                    String transactionId = exchange.getProperty(ExchangeHeader.TRANSACTION_ID.getKey(), String.class);
                    TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);
                    return transactionContext.isFailed();
                })
                .to("direct:respondChannelTransactions") //TODO failed
                .end(); // nothing to do, payer will continue the workflow
        ;

        from("direct:putQuotesResponse")
                .id("putQuotesResponse")
                .process(exchange -> {
                    Message in = exchange.getIn();
                    QuoteSwitchResponseDTO responseDTO = in.getBody(QuoteSwitchResponseDTO.class);
                    logger.debug(String.format("Incoming quotes response: %s", JsonUtil.toJson(responseDTO)));

                    Ilp ilp = ilpBuilderDpc.parse(responseDTO.getIlpPacket(), responseDTO.getCondition());
                    String transactionId = ilp.getTransaction().getTransactionId();
                    transactionContextHolder.updateSwitchQuote(transactionId, PAYEE, responseDTO, ilp); // role who created the response

                    TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);
                    transactionContext.populateExchangeContext(PAYER, exchange); // current role
                })
                .to("seda:quotesResponseQueue?waitForTaskToComplete=Never")
        ;
        from("seda:quotesResponseQueue")
                .id("quotesResponseQueue")
                .choice()
                .when(exchange -> {
                    String transactionId = exchange.getProperty(ExchangeHeader.TRANSACTION_ID.getKey(), String.class);
                    TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);
                    return transactionContext.isFailed();
                })
                    .to("direct:respondChannelTransactions") //TODO failed
                .otherwise()
                    .to("direct:getFspQuotes")
                .end();
        ;

        from("direct:putTransferResponse")
                .id("putTransferResponse")
                .process(exchange -> {
                    Message in = exchange.getIn();
                    TransferSwitchResponseDTO responseDTO = in.getBody(TransferSwitchResponseDTO.class);
                    logger.debug(String.format("Incoming transfer response: %s", JsonUtil.toJson(responseDTO)));

                    HttpServletRequest httpServletRequest = in.getBody(HttpServletRequest.class);
                    String pathInfo = httpServletRequest.getPathInfo();
                    logger.debug(String.format("Incoming transfer response path: %s", pathInfo));

                    String transferId = ContextUtil.parsePathParam(pathInfo, 1, 0);

                    String transactionId = transactionContextHolder.getTransactionByTransfer(transferId);
                    TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);
                    ilpBuilderDpc.validateFulfillmentAgainstCondition(responseDTO.getFulfilment(), transactionContext.getIlp().getCondition());

                    transactionContextHolder.updateSwitchTransfer(transactionId, PAYEE, responseDTO); // role who created the response

                    FspId currentFsp = exchange.getProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), FspId.class);
                    transactionContext.populateExchangeContext(currentFsp, exchange); // current role, must be PAYER
                })
                .choice()
                .when(exchange -> {
                    String transactionId = exchange.getProperty(ExchangeHeader.TRANSACTION_ID.getKey(), String.class);
                    TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);
                    String transferId = transactionContext.getTransferId();
                    TransferState transferState = transactionContext.getTransferState();

                    FspId fspId = exchange.getProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), FspId.class);
                    TransactionRoleContext roleContext = transactionContext.getRoleContext(fspId);
                    if (roleContext.getRole() != PAYER) {
                        logger.error(String.format("Incoming transfer response for PAYEE is invalid. " +
                                "Transaction: %s, transfer: %s, status: %s, fsp: %s", transactionId, transferId, transferState, fspId));
                        return false;
                    }

                    TransactionAction lastAction = roleContext.getLastAction();
                    boolean validAction = TransactionAction.isValidAction(lastAction, TransactionAction.COMMIT);
                    if (!validAction) {
                        logger.error(String.format("Incoming transfer response while the transfer was already committed. " +
                                "Transaction: %s, transfer: %s, status: %s, fsp: %s", transactionId, transferId, transferState, fspId));
                        return false;
                    }
                    return true; // TODO fix this, why called twice
                })
                    .to("seda:transfersResponseQueue?waitForTaskToComplete=Never")
        ;
        from("seda:transfersResponseQueue")
                .id("transfersResponseQueue")
                .choice()
                .when(exchange -> {
                    String transactionId = exchange.getProperty(ExchangeHeader.TRANSACTION_ID.getKey(), String.class);
                    TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);
                    return transactionContext.isFailed();
                })
                    .to("direct:respondChannelTransactions") //TODO failed
                .otherwise()
                    .to("direct:commitFspTransfer")
                .end();
        ;
    }

    private void buildParticipantRoute(String tenant) {
        TenantProperties binding = switchSettings.getBinding(SwitchSettings.SwitchBinding.PARTICIPANTS, tenant);
        String url = binding.getUrl() + "/{idType}/{idValue}";

        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.PUT;

        String instance = hubSettings.getInstance();
        from(consumerEndpoint)
                .id(tenant + "-switch-participant-put-rest-consumer")
                .unmarshal().json(JsonLibrary.Jackson, ParticipantSwitchResponseDTO.class)
                .process(exchange -> exchange.setProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), new FspId(instance, tenant)))
        ;

        //TODO: error handling, rollback in FSP when the switch response contains an error
    }

    private void buildPartiesRoute(String tenant) {
        TenantProperties binding = switchSettings.getBinding(SwitchSettings.SwitchBinding.PARTIES, tenant);
        String url = binding.getUrl() + "/{idType}/{idValue}";

        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.PUT;

        String instance = hubSettings.getInstance();
        from(consumerEndpoint)
                .id(tenant + "-switch-parties-put-rest-consumer")
                .unmarshal().json(JsonLibrary.Jackson, PartySwitchResponseDTO.class)
                .process(exchange -> exchange.setProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), new FspId(instance, tenant)))
                .to("direct:putPartiesResponse")
        ;

        url = url + "/{subIdOrType}";
        consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.PUT;

        from(consumerEndpoint)
                .id(tenant + "-switch-parties-put-subid-rest-consumer")
                .unmarshal().json(JsonLibrary.Jackson, PartySwitchResponseDTO.class)
                .process(exchange -> exchange.setProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), new FspId(instance, tenant)))
                .to("direct:putPartiesResponse")
        ;
    }

    private void buildTransactionRequestRoute(String tenant) {
        TenantProperties binding = switchSettings.getBinding(SwitchSettings.SwitchBinding.REQUESTS, tenant);
        String url = binding.getUrl() + "/{requestId}";

        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.PUT;

        String instance = hubSettings.getInstance();
        from(consumerEndpoint)
                .id(tenant + "-switch-requests-put-rest-consumer")
                .unmarshal().json(JsonLibrary.Jackson, TransactionRequestSwitchResponseDTO.class)
                .process(exchange -> exchange.setProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), new FspId(instance, tenant)))
                .to("direct:putTransactionRequestResponse")
        ;
    }

    private void buildQuotesRoute(String tenant) {
        TenantProperties binding = switchSettings.getBinding(SwitchSettings.SwitchBinding.QUOTES, tenant);
        String url = binding.getUrl() + "/{quoteId}";

        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.PUT;

        String instance = hubSettings.getInstance();
        from(consumerEndpoint)
                .id(tenant + "-switch-quotes-put-rest-consumer")
                .unmarshal().json(JsonLibrary.Jackson, QuoteSwitchResponseDTO.class)
                .process(exchange -> exchange.setProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), new FspId(instance, tenant)))
                .to("direct:putQuotesResponse")
        ;
    }

    private void buildTransferRoute(String tenant) {
        TenantProperties binding = switchSettings.getBinding(SwitchSettings.SwitchBinding.TRANSFERS, tenant);
        String url = binding.getUrl() + "/{transferId}";

        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.PUT;

        String instance = hubSettings.getInstance();
        from(consumerEndpoint)
                .id(tenant + "-switch-transfer-put-rest-consumer")
                .unmarshal().json(JsonLibrary.Jackson, TransferSwitchResponseDTO.class)
                .process(exchange -> {
                    exchange.setProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), new FspId(instance, tenant));
    })
                .to("direct:putTransferResponse")
        ;
    }
}
