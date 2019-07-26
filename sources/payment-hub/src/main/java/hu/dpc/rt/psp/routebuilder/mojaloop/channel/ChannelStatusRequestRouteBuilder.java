/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.routebuilder.mojaloop.channel;

import hu.dpc.rt.psp.cache.TransactionContextHolder;
import hu.dpc.rt.psp.config.BindingProperties;
import hu.dpc.rt.psp.config.ChannelSettings;
import hu.dpc.rt.psp.dto.channel.TransactionChannelAsyncResponseDTO;
import hu.dpc.rt.psp.internal.TransactionCacheContext;
import hu.dpc.rt.psp.util.ContextUtil;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Collection of GET /transactions endpoints called from the client channel.
 * After the transaction was triggered and synchronous OK response was sent back with the generated transaction identifier,
 * the client can repeatedly call one of the endpoints listed here to query the state os the transaction until it is committed.
 * Other possibility for the channel is to wait for the asynchronous callback from the payment-hub.
 */
@Configuration
public class ChannelStatusRequestRouteBuilder extends RouteBuilder {

    private static Logger logger = LoggerFactory.getLogger(ChannelStatusRequestRouteBuilder.class);

    private ChannelSettings channelSettings;

    private TransactionContextHolder transactionContextHolder;

    @Autowired
    public ChannelStatusRequestRouteBuilder(CamelContext camelContext, ChannelSettings channelSettings,
                                            TransactionContextHolder transactionContextHolder) {
        super(camelContext);
        this.channelSettings = channelSettings;
        this.transactionContextHolder = transactionContextHolder;
    }

    @Override
    public void configure() throws Exception {
        getContext().getShutdownStrategy().setTimeout(1);

        buildTransactionStatusByTransactionIdRoute();
        buildTransactionStatusByChannelClientRefRoute();
    }

    public void buildTransactionStatusByTransactionIdRoute() {
        BindingProperties binding = channelSettings.getBinding(ChannelSettings.ChannelBinding.STATUS);
        String url = binding.getUrl() + "/{hubTransactionId}";

        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.GET
                + "&enableCORS=" + channelSettings.isCorsEnabled();

        from(consumerEndpoint)
                .id("channel-transaction-status-rest-consumer")
                .to("direct:channel-transaction-status")
        ;

        from("direct:channel-transaction-status")
                .id("channel-transaction-status")
                .process(exchange -> {
                    String pathInfo = exchange.getIn().getBody(HttpServletRequest.class).getPathInfo();
                    String transactionId = ContextUtil.parsePathParam(pathInfo, 1, 0);

                    logger.debug(String.format("Extracted hub transaction ID from endpoint %s: %s", pathInfo, transactionId));

                    if (Strings.isEmpty(transactionId))
                        throw new RuntimeException("Missing transaction ID parameter");

                    TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);
                    if (transactionContext == null)
                        throw new RuntimeException("Not found transaction by id " + transactionId);

                    TransactionChannelAsyncResponseDTO paymentAsyncResponseDTO = new TransactionChannelAsyncResponseDTO(transactionContext.getChannelClientRef(),
                            transactionId, transactionContext.getCompletedStamp(), transactionContext.getTransferState(),
                            transactionContext.getPaymentRequestDTO());

                    exchange.getIn().setBody(paymentAsyncResponseDTO);
                })
                .marshal().json(JsonLibrary.Jackson)
        ;
    }

    public void buildTransactionStatusByChannelClientRefRoute() {
        BindingProperties binding = channelSettings.getBinding(ChannelSettings.ChannelBinding.CLIENT_STATUS);
        String url = binding.getUrl() + "/{channelClientRef}";

        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.GET
                + "&enableCORS=" + channelSettings.isCorsEnabled();

        from(consumerEndpoint)
                .id("channel-client-transaction-status-rest-consumer")
                .to("direct:channel-client-transaction-status")
        ;

        from("direct:channel-client-transaction-status")
                .id("channel-client-transaction-status")
                .process(exchange -> {
                    String pathInfo = exchange.getIn().getBody(HttpServletRequest.class).getPathInfo();
                    String channelClientRef = ContextUtil.parsePathParam(pathInfo, 1, 0);

                    logger.debug(String.format("Extracted hub channelClientRef from endpoint %s: %s", pathInfo, channelClientRef));

                    if (Strings.isEmpty(channelClientRef))
                        throw new RuntimeException("Missing channelClientRef parameter");

                    TransactionCacheContext transactionContext = transactionContextHolder.getContextByChannelClientRef(channelClientRef);
                    if (transactionContext == null)
                        throw new RuntimeException("Not found transaction by channelClientRef " + channelClientRef);

                    TransactionChannelAsyncResponseDTO paymentAsyncResponseDTO = new TransactionChannelAsyncResponseDTO(channelClientRef,
                            transactionContext.getTransactionId(), transactionContext.getCompletedStamp(), transactionContext.getTransferState(),
                            transactionContext.getPaymentRequestDTO());

                    exchange.getIn().setBody(paymentAsyncResponseDTO);
                })
                .marshal().json(JsonLibrary.Jackson)
        ;
    }
}
