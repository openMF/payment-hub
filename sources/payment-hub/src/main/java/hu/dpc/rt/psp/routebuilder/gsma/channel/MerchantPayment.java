package hu.dpc.rt.psp.routebuilder.gsma.channel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class MerchantPayment extends RouteBuilder {

    /*
     * What should be the routes? First, someone makes a payment through their local app - that is done through
     * Fineract in our case. That request is received by the hub and forwarded to the server.
     * What also needs to happen is checking if the transaction is completed successfully or not.
     */


    @Override
    public void configure() throws Exception {

        String port = "8081";
        String url = "http://0.0.0.0:" + port + "/merchantpayment";

        String clientUrl = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.POST + "&enableCORS=true";

        from(clientUrl)
                .id("receiveMerchantPaymentRequest")
                .log("Request received")
                .process(exchange -> {
                    exchange.setProperty("clientUrl", url);
                })
                .to("direct:commitTransaction")
        ;

        from("direct:commitTransaction")
            .id("commitTransaction")
            .log("Committing transaction...")
            .process("postTransactionsProcessor")
            .to("direct:giveConfirmation")
        ;

        from("direct:giveConfirmation")
                .id("giveConfirmation")
                .log("Checking transaction status...")
                .process("checkTransactionsProcessor")
        ;
    }
}
