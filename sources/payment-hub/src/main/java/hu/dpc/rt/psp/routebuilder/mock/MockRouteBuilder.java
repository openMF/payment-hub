/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.routebuilder.mock;

import hu.dpc.rt.psp.component.FspRestClient;
import hu.dpc.rt.psp.config.FspSettings;
import hu.dpc.rt.psp.config.MockSettings;
import hu.dpc.rt.psp.config.SwitchSettings;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockRouteBuilder extends RouteBuilder {

    private static Logger logger = LoggerFactory.getLogger(MockRouteBuilder.class);


    @Autowired
    private SwitchSettings switchSettings;

    @Autowired
    private FspSettings fspSettings;

    @Autowired
    private MockSettings mockSettings;

    @Autowired
    private FspRestClient fspRestClient;

    @Override
    public void configure() throws Exception {


        //Mojaloop mocks
//        if(mockSettings.isStartMojaloopConsumers()) {
//            from("jetty:http://0.0.0.0:" + switchSettings.getPort() + "/transfers?httpMethodRestrict=POST")
//                    .id("mojaloop-transfers-mock-consumer")
//                    .log("mojaloop-transfers-mock-consumer called!")
//                    .unmarshal().json(JsonLibrary.Jackson, TransferSwitchRequestDTO.class)
//                    .process(exchange -> {
//                        TransferSwitchRequestDTO transferSwitchRequestDTO = exchange.getIn().getBody(TransferSwitchRequestDTO.class);
//                    })
//            ;
//        }
//
//        if(mockSettings.isStartFspConsumers()) {
//            //FSP mocks
//            from("jetty:http://0.0.0.0:" + fspSettings.getPort() + "/interoperation/v1/quotes?httpMethodRestrict=POST")
//                    .id(fspSettings.getFspInstance() + "-fsp-quotes-mock-consumer")
//                    .log(fspSettings.getFspInstance() + "-fsp-quotes-mock-consumer-called!")
//                    .unmarshal().json(JsonLibrary.Jackson, QuoteFspRequestDTO.class)
//                    .process(exchange -> {
//                        QuoteFspRequestDTO quotesRequestDTO = exchange.getIn().getBody(QuoteFspRequestDTO.class);
//
//                        QuoteFspResponseDTO quotesResponseDTO = new QuoteFspResponseDTO();
//
//                        quotesResponseDTO.setTransactionCode(quotesRequestDTO.getTransactionCode());
//                        quotesResponseDTO.setQuoteCode(quotesRequestDTO.getQuoteCode());
//                        quotesResponseDTO.setExpiration(LocalDateTime.now());
//                        //TODO: calculate fee based on the transferAmount
//                        BigDecimal fee = quotesRequestDTO.getAmount().getAmount().multiply(new BigDecimal(0.01));
//                        quotesResponseDTO.setFspFee(new MoneyData(fee, quotesRequestDTO.getAmount().getCurrency()));
//                        quotesResponseDTO.setFspCommission(null);
//
//                        exchange.getIn().setBody(quotesResponseDTO);
//                    })
//                    .marshal().json(JsonLibrary.Jackson)
//            ;
//
//            from("jetty:http://0.0.0.0:" + fspSettings.getPort() + "/interoperation/v1/transfers?httpMethodRestrict=POST")
//                    .id(fspSettings.getFspInstance() + "-fsp-transfers-mock-consumer")
//                    .log(fspSettings.getFspInstance() + "-fsp-transfers-mock-consumer-called!")
//                    .unmarshal().json(JsonLibrary.Jackson, TransferFspRequestDTO.class)
//                    .process(exchange -> {
//                        TransferFspRequestDTO transferRequestDTO = exchange.getIn().getBody(TransferFspRequestDTO.class);
//
//
//                        TransferFspResponseDTO transferResponseDTO = new TransferFspResponseDTO();
//                        transferResponseDTO.setTransactionCode(transferRequestDTO.getTransactionCode());
//                        transferResponseDTO.setTransferCode(transferRequestDTO.getTransferCode());
//                        //TODO: completedTimestamp only at transactionCommit?
//                        transferResponseDTO.setCompletedTimestamp(LocalDateTime.now());
//
//                        exchange.getIn().setBody(transferResponseDTO);
//                    })
//                    .marshal().json(JsonLibrary.Jackson)
//            ;
//
//            //Identity/token mock
//            from("jetty:http://0.0.0.0:" + fspSettings.getPort() + "/identity/v1/token?httpMethodRestrict=POST")
//                    .id(fspSettings.getFspInstance() + "-fsp-identity-tokens-mock-consumer")
//                    .log(fspSettings.getFspInstance() + "-fsp-identity-tokens-mock-consumer-called!")
//                    .process(exchange -> {
//                        LoginFspResponseDTO loginResponseDTO = new LoginFspResponseDTO();
//
//                        loginResponseDTO.setAccessToken("Bearer header.payload.signature");
//                        loginResponseDTO.setAccessTokenExpiration(new Date(System.currentTimeMillis() + 1000*60*60*24*30));
//
//                        exchange.getIn().setBody(loginResponseDTO);
//                    })
//                    .marshal().json(JsonLibrary.Jackson)
//            ;
//
//
//        }
//
//        if(mockSettings.isStartChannelConsumers()) {
//            //Channel mocks
//            fspSettings.getFspTenants().forEach(fspTenant -> {
//                String callbackUrl = fspTenant.getChannelPaymentResponseCallback();
//                Integer port;
//                try {
//                    port = new URL(callbackUrl).getPort();
//                } catch (MalformedURLException e) {
//                    throw new RuntimeException("Could not determine port of the ChannelPaymentResponseCallback URL.", e);
//                }
//                from("jetty:http://0.0.0.0:" + port + "/interoperation/transactions?httpMethodRestrict=PUT")
//                        .id(fspTenant.getFspTenant() + "-channel-transactions-mock-consumer")
//                        .log(fspTenant.getFspTenant() + "-channel-transactions-mock-consumer called!")
//                        .unmarshal().json(JsonLibrary.Jackson, TransactionChannelAsyncResponseDTO.class)
//                        .process(exchange -> {
//
//                        })
//                ;
//            });
//        }

        /*from("jetty:http://0.0.0.0:" + fspSettings.getPort() + "/login?httpMethodRestrict=GET")
                .id(fspSettings.getFspInstance() + "-fsp-login-mock-consumer")
                .log(fspSettings.getFspInstance() + "-fsp-login-mock-consumer-called!")
                .process(exchange -> {

                    fspRestClient.login("tenant-1");
                })
        ;*/
    }
}
