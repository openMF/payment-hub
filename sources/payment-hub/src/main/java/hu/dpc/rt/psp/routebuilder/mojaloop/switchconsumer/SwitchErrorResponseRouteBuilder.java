/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.routebuilder.mojaloop.switchconsumer;

import hu.dpc.rt.psp.cache.TransactionContextHolder;
import hu.dpc.rt.psp.component.IlpBuilder;
import hu.dpc.rt.psp.config.HubSettings;
import hu.dpc.rt.psp.config.SwitchSettings;
import hu.dpc.rt.psp.config.TenantProperties;
import hu.dpc.rt.psp.constant.ExchangeHeader;
import hu.dpc.rt.psp.dto.mojaloop.ErrorSwitchResponseDTO;
import hu.dpc.rt.psp.dto.mojaloop.QuoteSwitchResponseDTO;
import hu.dpc.rt.psp.internal.FspId;
import hu.dpc.rt.psp.util.JsonUtil;
import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

/**
 * List of asynchronous PUT error callback endpoints as response to GET, POST requests.
 * @see SwitchResponseRouteBuilder
 */
@Configuration
public class SwitchErrorResponseRouteBuilder extends RouteBuilder {

    private static Logger logger = LoggerFactory.getLogger(SwitchErrorResponseRouteBuilder.class);

    private HubSettings hubSettings;
    private SwitchSettings switchSettings;

    private TransactionContextHolder transactionContextHolder;

    private IlpBuilder ilpBuilder;

    @Autowired
    public SwitchErrorResponseRouteBuilder(CamelContext camelContext, HubSettings hubSettings, SwitchSettings switchSettings,
                                      TransactionContextHolder transactionContextHolder, IlpBuilder ilpBuilder) {
        super(camelContext);
        this.hubSettings = hubSettings;
        this.switchSettings = switchSettings;
        this.transactionContextHolder = transactionContextHolder;
        this.ilpBuilder = ilpBuilder;
    }

    @Override
    public void configure() throws Exception {
        getContext().getShutdownStrategy().setTimeout(1);

        hubSettings.getTenants().forEach(tenant -> {
            buildPartiesErrorRoute(tenant);
            buildTransactionRequestErrorRoute(tenant);
            buildQuotesErrorRoute(tenant);
            buildTransferErrorRoute(tenant);
            buildParticipantErrorRoute(tenant);
        });

        from("direct:putParticipantErrorResponse")
                .id("putParticipantErrorResponse")
                .process(exchange -> {
                })
                .to("direct:logErrorResponse")
                .to("seda:putParticipantErrorResponseQueue?waitForTaskToComplete=Never")
        ;
        from("seda:putParticipantErrorResponseQueue")
                .id("putParticipantErrorResponseQueue")
                .to("direct:respondChannelTransactions")
        ;

        from("direct:putPartiesErrorResponse")
                .id("putPartiesErrorResponse")
                .process(exchange -> {
                })
                .to("direct:logErrorResponse")
                .to("seda:putPartiesErrorResponseQueue?waitForTaskToComplete=Never")
        ;
        from("seda:putPartiesErrorResponseQueue")
                .id("putPartiesErrorResponseQueue")
                .to("direct:respondChannelTransactions")
        ;

        from("direct:putTransactionRequestErrorResponse")
                .id("putTransactionRequestErrorResponse")
                .process(exchange -> {
                })
                .to("direct:logErrorResponse")
                .to("seda:putTransactionRequestErrorResponseQueue?waitForTaskToComplete=Never")
        ;
        from("seda:putTransactionRequestErrorResponseQueue")
                .id("putTransactionRequestErrorResponseQueue")
                .to("direct:respondChannelTransactions")
        ;

        from("direct:putQuotesErrorResponse")
                .id("putQuotesErrorResponse")
                .process(exchange -> {
                })
                .to("direct:logErrorResponse")
                .to("seda:putQuotesErrorResponseQueue?waitForTaskToComplete=Never")
        ;
        from("seda:putQuotesErrorResponseQueue")
                .id("putQuotesErrorResponseQueue")
                .to("direct:respondChannelTransactions")
        ;

        from("direct:putTransferErrorResponse")
                .id("putTransferErrorResponse")
                .process(exchange -> {
                })
                .to("direct:logErrorResponse")
                .to("seda:putTransferErrorResponseQueue?waitForTaskToComplete=Never")
        ;
        from("seda:putTransferErrorResponseQueue")
                .id("putTransferErrorResponseQueue")
                .to("direct:respondChannelTransactions")
        ;

        from("direct:logErrorResponse")
                .process(exchange -> {
                    Message in = exchange.getIn();
                    QuoteSwitchResponseDTO responseDTO = in.getBody(QuoteSwitchResponseDTO.class);
                    logger.debug(String.format("Incoming ERROR: %s", JsonUtil.toJson(responseDTO)));
                })
        ;
    }

    private void buildParticipantErrorRoute(String tenant) {
        TenantProperties binding = switchSettings.getBinding(SwitchSettings.SwitchBinding.PARTICIPANTS, tenant);
        String url = binding.getUrl() + "/{transferId}/error";

        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.PUT;

        String instance = hubSettings.getInstance();
        from(consumerEndpoint)
                .id(tenant + "-switch-participant-put-error-rest-consumer")
                .unmarshal().json(JsonLibrary.Jackson, ErrorSwitchResponseDTO.class)
                .process(exchange -> exchange.setProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), new FspId(instance, tenant)))
                .to("direct:putParticipantErrorResponse")
        ;

        //TODO: error handling, rollback in FSP when the switch response contains an error
    }

    private void buildPartiesErrorRoute(String tenant) {
        TenantProperties binding = switchSettings.getBinding(SwitchSettings.SwitchBinding.PARTIES, tenant);
        String baseUrl = binding.getUrl();
        String url = baseUrl + "/{idType}/{idValue}/error";
        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.PUT;

        String instance = hubSettings.getInstance();
        from(consumerEndpoint)
                .id(tenant + "-switch-parties-put-error-rest-consumer")
                .unmarshal().json(JsonLibrary.Jackson, ErrorSwitchResponseDTO.class)
                .process(exchange -> exchange.setProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), new FspId(instance, tenant)))
                .to("direct:putPartiesSubErrorResponse")
        ;

        url = baseUrl + "/{idType}/{idValue}/{subIdOrType}/error";
        consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.PUT;

        from(consumerEndpoint)
                .id(tenant + "-switch-parties-put-error-subid-rest-consumer")
                .unmarshal().json(JsonLibrary.Jackson, ErrorSwitchResponseDTO.class)
                .process(exchange -> exchange.setProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), new FspId(instance, tenant)))
                .to("direct:putPartiesErrorResponse")
        ;
    }

    private void buildTransactionRequestErrorRoute(String tenant) {
        TenantProperties binding = switchSettings.getBinding(SwitchSettings.SwitchBinding.REQUESTS, tenant);
        String url = binding.getUrl() + "/{requestId}/error";

        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.PUT;

        String instance = hubSettings.getInstance();
        from(consumerEndpoint)
                .id(tenant + "-switch-requests-put-error-rest-consumer")
                .unmarshal().json(JsonLibrary.Jackson, ErrorSwitchResponseDTO.class)
                .process(exchange -> exchange.setProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), new FspId(instance, tenant)))
                .to("direct:putTransactionRequestErrorResponse")
        ;
    }

    private void buildQuotesErrorRoute(String tenant) {
        TenantProperties binding = switchSettings.getBinding(SwitchSettings.SwitchBinding.QUOTES, tenant);
        String url = binding.getUrl() + "/{quoteId}/error";

        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.PUT;

        String instance = hubSettings.getInstance();
        from(consumerEndpoint)
                .id(tenant + "-switch-quotes-put-error-rest-consumer")
                .unmarshal().json(JsonLibrary.Jackson, ErrorSwitchResponseDTO.class)
                .process(exchange -> exchange.setProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), new FspId(instance, tenant)))
                .to("direct:putQuotesErrorResponse")
        ;
    }

    private void buildTransferErrorRoute(String tenant) {
        TenantProperties binding = switchSettings.getBinding(SwitchSettings.SwitchBinding.TRANSFERS, tenant);
        String url = binding.getUrl() + "/{transferId}/error";

        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.PUT;

        String instance = hubSettings.getInstance();
        from(consumerEndpoint)
                .id(tenant + "-switch-transfer-put-error-rest-consumer")
                .unmarshal().json(JsonLibrary.Jackson, ErrorSwitchResponseDTO.class)
                .process(exchange -> exchange.setProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), new FspId(instance, tenant)))
                .to("direct:putTransferErrorResponse")
        ;
    }
}
