/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.routebuilder.switchconsumer;

import hu.dpc.rt.psp.cache.TransactionContextHolder;
import hu.dpc.rt.psp.component.IlpBuilderDpc;
import hu.dpc.rt.psp.config.HubSettings;
import hu.dpc.rt.psp.config.SwitchSettings;
import hu.dpc.rt.psp.config.TenantProperties;
import hu.dpc.rt.psp.constant.ExchangeHeader;
import hu.dpc.rt.psp.dto.PartyIdInfo;
import hu.dpc.rt.psp.dto.mojaloop.QuoteSwitchRequestDTO;
import hu.dpc.rt.psp.dto.mojaloop.TransactionRequestSwitchRequestDTO;
import hu.dpc.rt.psp.dto.mojaloop.TransferSwitchRequestDTO;
import hu.dpc.rt.psp.internal.FspId;
import hu.dpc.rt.psp.internal.Ilp;
import hu.dpc.rt.psp.internal.TransactionCacheContext;
import hu.dpc.rt.psp.internal.TransactionRoleContext;
import hu.dpc.rt.psp.type.IdentifierType;
import hu.dpc.rt.psp.type.TransactionRole;
import hu.dpc.rt.psp.util.ContextUtil;
import hu.dpc.rt.psp.util.JsonUtil;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;

import static hu.dpc.rt.psp.type.TransactionRole.PAYEE;
import static hu.dpc.rt.psp.type.TransactionRole.PAYER;
import static hu.dpc.rt.psp.util.ContextUtil.*;

/**
 * List of GET, POST endpoints which were originated from the other side FSP and were sent through the switch.
 * As a response of these calls the result are sent back will be processed by {@code SwitchResponseRouteBuilder}
 */
@Configuration
public class SwitchRequestRouteBuilder extends RouteBuilder {

    private static Logger logger = LoggerFactory.getLogger(SwitchRequestRouteBuilder.class);

    private HubSettings hubSettings;
    private SwitchSettings switchSettings;

    private TransactionContextHolder transactionContextHolder;

    private IlpBuilderDpc ilpBuilderDpc;

    public SwitchRequestRouteBuilder(CamelContext camelContext, HubSettings hubSettings, SwitchSettings switchSettings,
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
        });


        from("direct:getPartiesSubTypeGetRequest")
                .id("getPartiesSubTypeGetRequest")
                .process(exchange -> {
                    Message in = exchange.getIn();
                    HttpServletRequest httpServletRequest = in.getBody(HttpServletRequest.class);
                    String pathInfo = httpServletRequest.getPathInfo();
                    logger.debug(String.format("Incoming parties switch get request: %s", pathInfo));

                    IdentifierType idType = IdentifierType.valueOf(ContextUtil.parsePathParam(pathInfo, 3, 0));
                    String identifier = ContextUtil.parsePathParam(pathInfo, 3, 1);
                    String subIdOrType = ContextUtil.parsePathParam(pathInfo, 3, 2);

                    FspId currentFsp = exchange.getProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), FspId.class);
                    exchange.setProperty(ExchangeHeader.PARTIES_INFO.getKey(), new PartyIdInfo(idType, identifier, subIdOrType, currentFsp.getId()));

                    FspId callerFsp = ContextUtil.parseFspId(in.getHeader(switchSettings.getHeader(SwitchSettings.SwitchHeader.SOURCE).getKey(), String.class));
                    exchange.setProperty(ExchangeHeader.CALLER_FSP.getKey(), callerFsp);
                })
                .to("seda:partiesPostQueue?waitForTaskToComplete=Never")
                .process(exchange -> exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 202))
        ;
        from("direct:getPartiesGetRequest")
                .id("getPartiesGetRequest")
                .process(exchange -> {
                    Message in = exchange.getIn();
                    HttpServletRequest httpServletRequest = in.getBody(HttpServletRequest.class);
                    String pathInfo = httpServletRequest.getPathInfo();
                    logger.debug(String.format("Incoming parties switch get request: %s", pathInfo));

                    IdentifierType idType = IdentifierType.valueOf(ContextUtil.parsePathParam(pathInfo, 2, 0));
                    String identifier = ContextUtil.parsePathParam(pathInfo, 2, 1);

                    FspId currentFsp = exchange.getProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), FspId.class);
                    exchange.setProperty(ExchangeHeader.PARTIES_INFO.getKey(), new PartyIdInfo(idType, identifier, null, currentFsp.getId()));

                    FspId callerFsp = ContextUtil.parseFspId(in.getHeader(switchSettings.getHeader(SwitchSettings.SwitchHeader.SOURCE).getKey(), String.class));
                    exchange.setProperty(ExchangeHeader.CALLER_FSP.getKey(), callerFsp);
                })
                .to("seda:partiesPostQueue?waitForTaskToComplete=Never")
                .process(exchange -> exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 202))
        ;
        from("seda:partiesPostQueue")
                .id("partiesPostQueue")
                .to("direct:getFspParties")
        ;

        from("direct:getTransactionPostRequest")
                .id("getTransactionPostRequest")
                .process(exchange -> {
                    Message in = exchange.getIn();
                    TransactionRequestSwitchRequestDTO requestDTO = in.getBody(TransactionRequestSwitchRequestDTO.class);
                    logger.debug(String.format("Incoming transaction switch post request: %s", JsonUtil.toJson(requestDTO)));

                    String transactionId = requestDTO.getExtensionValue(EXTENSION_KEY_TRANSACTION_ID);

                    transactionContextHolder.updateSwitchTransactionRequest(transactionId, PAYEE, requestDTO); // sender role

                    TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);
                    transactionContext.populateExchangeContext(PAYER, exchange); // current role

                    FspId callerFsp = ContextUtil.parseFspId(in.getHeader(switchSettings.getHeader(SwitchSettings.SwitchHeader.SOURCE).getKey(), String.class));
                    exchange.setProperty(ExchangeHeader.CALLER_FSP.getKey(), callerFsp);
                })
                .to("seda:transactionRequestPostQueue?waitForTaskToComplete=Never")
                .process(exchange -> exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 202))
        ;
        from("seda:transactionRequestPostQueue")
                .id("transactionRequestPostQueue")
                .to("direct:getFspTransactionRequest")
                .to("direct:getSwitchQuotes")
        ;
        from("direct:getFspTransactionRequest")
                .id("getFspTransactionRequest")
                .process("transactionRequestFspProcessor")
                .process("transactionRequestResponseSwitchProcessor")
                .end()
        ;

        from("direct:getQuotesPostRequest")
                .id("getQuotesPostRequest")
                .process(exchange -> {
                    Message in = exchange.getIn();
                    QuoteSwitchRequestDTO requestDTO = in.getBody(QuoteSwitchRequestDTO.class);
                    logger.debug(String.format("Incoming quotes switch post request: %s", JsonUtil.toJson(requestDTO)));

                    String transactionId = requestDTO.getTransactionId();
                    FspId sourceFsp = ContextUtil.parseFspId(in.getHeader(switchSettings.getHeader(SwitchSettings.SwitchHeader.SOURCE).getKey(), String.class));
                    FspId destFsp = ContextUtil.parseFspId(in.getHeader(switchSettings.getHeader(SwitchSettings.SwitchHeader.DESTINATION).getKey(), String.class));

                    String note = requestDTO.getNote();
                    if (note != null) {
                        int idx = note.indexOf(EXTENSION_SEPARATOR);
                        if (idx > -1) {
                            String channelClientRef = note.substring(idx + EXTENSION_SEPARATOR.length());
                            requestDTO.setNote(note.substring(0, idx));
                            requestDTO.addExtension(EXTENSION_KEY_CHANNEL_CLIENT_REF, channelClientRef);
                        }
                    }
                    transactionContextHolder.updateSwitchQuoteRequest(transactionId, PAYER, requestDTO, sourceFsp, destFsp); // sender role

                    TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);
                    transactionContext.populateExchangeContext(PAYEE, exchange); // current role

                    exchange.setProperty(ExchangeHeader.CALLER_FSP.getKey(), sourceFsp);
                })
                .to("seda:quotesRequestPostQueue?waitForTaskToComplete=Never")
                .process(exchange -> exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 202))
        ;
        from("seda:quotesRequestPostQueue")
                .id("quotesRequestPostQueue")
                .choice()
                .when(exchange -> {
                    String transactionId = exchange.getProperty(ExchangeHeader.TRANSACTION_ID.getKey(), String.class);
                    TransactionRole currentRole = exchange.getProperty(ExchangeHeader.CURRENT_ROLE.getKey(), TransactionRole.class);
                    TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);
                    TransactionRoleContext currentContex = transactionContext.getRoleContext(currentRole);
                    if (currentContex.getPartyContext().getAccountId() == null) {
                        exchange.setProperty(ExchangeHeader.PARTIES_INFO.getKey(), currentContex.getPartyContext().getParty().getPartyIdInfo());
                        return true;
                    }
                    return false;
                })
                .process("partiesFspProcessor")
                .process(exchange -> {
                    exchange.setProperty(ExchangeHeader.PARTIES_INFO.getKey(), null);
                })
                .end()
                .to("direct:getFspQuotes")
        ;

        from("direct:getTransferPostRequest")
                .id("getTransferPostRequest")
                .process(exchange -> {
                    Message in = exchange.getIn();
                    TransferSwitchRequestDTO requestDTO = in.getBody(TransferSwitchRequestDTO.class);
                    logger.debug(String.format("Incoming transfer switch post request: %s", JsonUtil.toJson(requestDTO)));

                    Ilp ilp = ilpBuilderDpc.parse(requestDTO.getIlpPacket(), requestDTO.getCondition());
                    String transactionId = ilp.getTransaction().getTransactionId();

                    FspId sourceFsp = ContextUtil.parseFspId(in.getHeader(switchSettings.getHeader(SwitchSettings.SwitchHeader.SOURCE).getKey(), String.class));
                    FspId destFsp = ContextUtil.parseFspId(in.getHeader(switchSettings.getHeader(SwitchSettings.SwitchHeader.DESTINATION).getKey(), String.class));
                    transactionContextHolder.updateSwitchTransferRequest(transactionId, PAYER, requestDTO, ilp, sourceFsp, destFsp); // sender role

                    TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);
                    transactionContext.populateExchangeContext(TransactionRole.PAYEE, exchange); // current role

                    exchange.setProperty(ExchangeHeader.CALLER_FSP.getKey(), sourceFsp);
                })
                .to("seda:transfersRequestPostQueue?waitForTaskToComplete=Never")
                .process(exchange -> exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 202))
        ;
        from("seda:transfersRequestPostQueue")
                .id("transfersRequestPostQueue")
                .to("direct:commitFspTransfer")
        ;
    }

    private void buildPartiesRoute(String tenant) {
        TenantProperties binding = switchSettings.getBinding(SwitchSettings.SwitchBinding.PARTIES, tenant);
        String url = binding.getUrl() + "/{idType}/{idValue}";

        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.GET;

        String instance = hubSettings.getInstance();
        from(consumerEndpoint)
                .id(tenant + "-switch-parties-get-rest-consumer")
                .process(exchange -> exchange.setProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), new FspId(instance, tenant)))
                .to("direct:getPartiesGetRequest")
        ;

        url = url + "/{subIdOrType}";

        consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.GET;

        from(consumerEndpoint)
                .id(tenant + "-switch-parties-get-subid-rest-consumer")
                .process(exchange -> exchange.setProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), new FspId(instance, tenant)))
                .to("direct:getPartiesSubTypeGetRequest")
        ;
    }

    private void buildTransactionRequestRoute(String tenant) {
        TenantProperties binding = switchSettings.getBinding(SwitchSettings.SwitchBinding.REQUESTS, tenant);
        String url = binding.getUrl();

        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.POST;

        String instance = hubSettings.getInstance();
        from(consumerEndpoint)
                .id(tenant + "-switch-requests-get-rest-consumer")
                .unmarshal().json(JsonLibrary.Jackson, TransactionRequestSwitchRequestDTO.class)
                .process(exchange -> exchange.setProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), new FspId(instance, tenant)))
                .to("direct:getTransactionPostRequest")
        ;
    }

    private void buildQuotesRoute(String tenant) {
        TenantProperties binding = switchSettings.getBinding(SwitchSettings.SwitchBinding.QUOTES, tenant);
        String url = binding.getUrl();

        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.POST;

        String instance = hubSettings.getInstance();
        from(consumerEndpoint)
                .id(tenant + "-switch-quotes-get-rest-consumer")
                .unmarshal().json(JsonLibrary.Jackson, QuoteSwitchRequestDTO.class)
                .process(exchange -> exchange.setProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), new FspId(instance, tenant)))
                .to("direct:getQuotesPostRequest")
        ;
    }

    private void buildTransferRoute(String tenant) {
        TenantProperties binding = switchSettings.getBinding(SwitchSettings.SwitchBinding.TRANSFERS, tenant);
        String url = binding.getUrl();

        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.POST;

        String instance = hubSettings.getInstance();
        from(consumerEndpoint)
                .id(tenant + "-switch-transfer-get-rest-consumer")
                .unmarshal().json(JsonLibrary.Jackson, TransferSwitchRequestDTO.class)
                .process(exchange -> exchange.setProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), new FspId(instance, tenant)))
                .to("direct:getTransferPostRequest")
        ;
    }
}
