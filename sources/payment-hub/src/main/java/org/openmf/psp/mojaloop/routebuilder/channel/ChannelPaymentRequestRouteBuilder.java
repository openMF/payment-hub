/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.mojaloop.routebuilder.channel;

import static org.openmf.psp.type.TransactionRole.PAYEE;
import static org.openmf.psp.type.TransactionRole.PAYER;

import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.openmf.psp.config.BindingProperties;
import org.openmf.psp.config.ChannelSettings;
import org.openmf.psp.config.HubSettings;
import org.openmf.psp.dto.PartyIdInfo;
import org.openmf.psp.dto.channel.TransactionChannelRequestDTO;
import org.openmf.psp.dto.channel.TransactionChannelResponseDTO;
import org.openmf.psp.internal.FspId;
import org.openmf.psp.mojaloop.cache.TransactionContextHolder;
import org.openmf.psp.mojaloop.constant.ExchangeHeader;
import org.openmf.psp.mojaloop.internal.TransactionCacheContext;
import org.openmf.psp.mojaloop.internal.TransactionRoleContext;
import org.openmf.psp.type.TransactionRole;
import org.openmf.psp.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

/**
 * Initial /transactions endpoint called from channel, which is the first triggering step of the payment operation.
 * Functional nodes triggered by any callback endpoints are also listed here in the order of interoperation work-flow.
 */
@Configuration
public class ChannelPaymentRequestRouteBuilder extends RouteBuilder {

    private static Logger logger = LoggerFactory.getLogger(ChannelPaymentRequestRouteBuilder.class);

    private HubSettings hubSettings;
    private ChannelSettings channelSettings;

    private TransactionContextHolder transactionContextHolder;

    @Autowired
    public ChannelPaymentRequestRouteBuilder(CamelContext camelContext, HubSettings hubSettings, ChannelSettings channelSettings,
                                             TransactionContextHolder transactionContextHolder) {
        super(camelContext);
        this.hubSettings = hubSettings;
        this.channelSettings = channelSettings;
        this.transactionContextHolder = transactionContextHolder;
    }

    @Override
    public void configure() throws Exception {
        getContext().getShutdownStrategy().setTimeout(1);

        BindingProperties binding = channelSettings.getBinding(ChannelSettings.ChannelBinding.PAYMENT);
        String url = binding.getUrl();

        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.POST + "&enableCORS=" + channelSettings.isCorsEnabled();

        from(consumerEndpoint)
                .id("channel-transactions-rest-consumer")
                .unmarshal().json(JsonLibrary.Jackson, TransactionChannelRequestDTO.class)
                .to("direct:receiveChannelTransactions")
        ;

        from("direct:receiveChannelTransactions")
                .id("receiveChannelTransactions")
                .process(exchange -> {
                    Message in = exchange.getIn();
                    TransactionChannelRequestDTO paymentRequestDTO = in.getBody(TransactionChannelRequestDTO.class);
                    logger.debug(String.format("Incoming payment request: %s", JsonUtil.toJson(paymentRequestDTO)));

                    TransactionRole currentRole = paymentRequestDTO.getTransactionType().getInitiator();

                    TransactionCacheContext transactionCacheContext = transactionContextHolder.createTransactionCacheContext();
                    String transactionId = transactionCacheContext.getTransactionId();

                    logger.debug(String.format("Transaction id is (%s) for channel client ref (%s)", transactionId, paymentRequestDTO.getClientRefId()));

                    transactionContextHolder.updateChannelPaymentRequest(transactionId, paymentRequestDTO);

                    String instance = hubSettings.getInstance();
                    String tenant = in.getHeader(channelSettings.getHeader(ChannelSettings.ChannelHeader.TENANT.TENANT).getKey(), String.class);
                    transactionCacheContext.getRoleContext(currentRole).setFspId(new FspId(instance, tenant));

                    transactionCacheContext.populateExchangeContext(currentRole, exchange);
                })
                .to("seda:channelTransactionsQueue?waitForTaskToComplete=Never")
                .process(exchange -> {
                    TransactionChannelResponseDTO response = new TransactionChannelResponseDTO(exchange.getProperty(ExchangeHeader.TRANSACTION_ID.getKey(), String.class));
                    exchange.getIn().setBody(response);
                })
                .marshal().json(JsonLibrary.Jackson)
        ;

        from("seda:channelTransactionsQueue")
                .id("channelTransactionsQueue")
                .to("direct:getFspParties")
        ;

        from("direct:getFspParties")
                .id("getFspParties")
                .process(exchange -> {
                    logger.debug("Get Source Party information");
                    TransactionRole currentRole = exchange.getProperty(ExchangeHeader.CURRENT_ROLE.getKey(), TransactionRole.class);
                    exchange.setProperty(ExchangeHeader.PARTIES_ROLE.getKey(), currentRole);
                })
                .process("partiesFspProcessor")
                .process(exchange -> {
                    exchange.setProperty(ExchangeHeader.PARTIES_ROLE.getKey(), null);
                })
                .choice()
                .when(exchange -> exchange.getProperty(ExchangeHeader.CALLER_FSP.getKey()) != null)
                    .process("partiesSwitchResponseProcessor")
                .otherwise()
                    .to("direct:getSwitchParties")
                .end();
        ;

        from("direct:getSwitchParties")
                .id("getSwitchParties")
                .process(exchange -> {
                    logger.debug("Get Destination Party information");
                    TransactionRole currentRole = exchange.getProperty(ExchangeHeader.CURRENT_ROLE.getKey(), TransactionRole.class);
                    exchange.setProperty(ExchangeHeader.PARTIES_ROLE.getKey(), currentRole == PAYER ? PAYEE : PAYER);
                })
                .process("partiesSwitchProcessor")
                .choice()
                .when(exchange -> {
                    String transactionId = exchange.getProperty(ExchangeHeader.TRANSACTION_ID.getKey(), String.class);
                    TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);

                    TransactionRole partiesRole = exchange.getProperty(ExchangeHeader.PARTIES_ROLE.getKey(), TransactionRole.class);
                    exchange.setProperty(ExchangeHeader.PARTIES_ROLE.getKey(), null);

                    TransactionRoleContext roleContext = transactionContext.getRoleContext(partiesRole);
                    PartyIdInfo partyIdInfo = roleContext.getPartyContext().getParty().getPartyIdInfo();
                    if (partyIdInfo.getFspId() == null) {
                        transactionContextHolder.addWaitingPartyTransaction(partyIdInfo, transactionId);
                        return false;
                    }
                    return true;
                })
                    .to("direct:getSwitchTransactionRequest")
                .end();
        ;

        from("direct:getSwitchTransactionRequest")
                .id("getSwitchTransactionRequest")
                .choice()
                .when(exchange -> exchange.getProperty(ExchangeHeader.CURRENT_ROLE.getKey(), TransactionRole.class) == PAYEE)
                    .process(exchange -> {
                        logger.debug("Send Transaction Request to " + exchange.getProperty(ExchangeHeader.PAYEE_FSP_ID.getKey()));
                    })
                    .process("transactionRequestSwitchProcessor")
                .otherwise()
                    .to("direct:getSwitchQuotes")
                .end()
        ;

        from("direct:getSwitchQuotes")
                .id("getSwitchQuotes")
                .process(exchange -> {
                    logger.debug("Payee FSP ID: " + exchange.getProperty(ExchangeHeader.PAYEE_FSP_ID.getKey()));
                    exchange.setProperty(ExchangeHeader.QUOTES_ROLE.getKey(), PAYEE);
                })
                .process("quotesSwitchProcessor")
                .process(exchange -> {
                    exchange.setProperty(ExchangeHeader.QUOTES_ROLE.getKey(), null);
                })
                .choice()
                .when(exchange -> {
                    String transactionId = exchange.getProperty(ExchangeHeader.TRANSACTION_ID.getKey(), String.class);
                    TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);
                    return transactionContext.getRoleContext(PAYEE).getFspQuoteDTO() != null;
                })
                .to("direct:getFspQuotes")
                .end();
        ;

        from("direct:getFspQuotes")
                .id("getFspQuotes")
                .process(exchange -> {
                    logger.debug("Current FSP ID: " + exchange.getProperty(ExchangeHeader.CURRENT_FSP_ID.getKey()));
                })
                .process("quotesFspProcessor")
                .choice()
                .when(exchange -> exchange.getProperty(ExchangeHeader.CALLER_FSP.getKey()) != null)
                    .process("quotesSwitchResponseProcessor")
                .otherwise()
                    .to("direct:notifyPayerQuotes")
                .end();
        ;

        from("direct:notifyPayerQuotes")
                .id("notifyPayerQuotes")
                .process(exchange -> {
                    logger.info("Notifying the payer about the quotes.");
                })
                .to("direct:notifyPayeeQuotes")
        ;

        from("direct:notifyPayeeQuotes") // TODO: not needed
                .id("notifyPayeeQuotes")
                .process(exchange -> {
                    logger.info("Notifying the payee about the quotes.");
                })
                .to("direct:preparePayerTransfer")
        ;

        from("direct:preparePayerTransfer")
                .id("preparePayerTransfer")
                .process("prepareTransferFspProcessor")
                .to("direct:commitSwitchTransfer")
        ;

        from("direct:commitSwitchTransfer")
                .id("commitSwitchTransfer")
                .process(exchange -> {
                    logger.debug("Payee FSP ID: " + exchange.getProperty(ExchangeHeader.PAYEE_FSP_ID.getKey()));
                    exchange.setProperty(ExchangeHeader.TRANSFER_ROLE.getKey(), PAYEE);
                })
                .process("commitTransferSwitchProcessor")
                .process(exchange -> {
                    exchange.setProperty(ExchangeHeader.TRANSFER_ROLE.getKey(), null);
                })
                .choice()
                .when(exchange -> {
                    String transactionId = exchange.getProperty(ExchangeHeader.TRANSACTION_ID.getKey(), String.class);
                    TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);
                    return transactionContext.getRoleContext(PAYEE).getFspTransferDTO() != null;
                })
                .to("direct:commitFspTransfer")
                .end();
        ;

        from("direct:commitFspTransfer")
                .id("commitFspTransfer")
                .process("commitTransferFspProcessor")
                .choice()
                .when(exchange -> exchange.getProperty(ExchangeHeader.CALLER_FSP.getKey()) != null)
                    .process("transfersSwitchResponseProcessor")
                .otherwise()
                    .to("direct:respondChannelTransactions")
                .end();
        ;

        from("direct:respondChannelTransactions")
                .id("respondChannelTransactions")
                .process("channelTransactionsResponseProcessor")
        ;
    }
}
