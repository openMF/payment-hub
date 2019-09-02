package hu.dpc.rt.psp.routebuilder.gsma.channel;

import hu.dpc.rt.psp.config.*;
import hu.dpc.rt.psp.dto.gsma.TransactionObject;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class MerchantPayment extends RouteBuilder {

    /*
     * What should be the routes? First, someone makes a payment through their local app - that is done through
     * Fineract in our case. That request is received by the hub and forwarded to the server.
     * What also needs to happen is checking if the transaction is completed successfully or not.
     */

    private HubSettings hubSettings;
    private OttSettings ottSettings;

    @Autowired
    public MerchantPayment (CamelContext camelContext, HubSettings hubSettings, OttSettings ottSettings) {

        super(camelContext);
        this.hubSettings = hubSettings;
        this.ottSettings = ottSettings;
    }

    @Override
    public void configure() throws Exception {
        getContext().getShutdownStrategy().setTimeout(1);

        OperationProperties transactionsOperation = ottSettings.getOperation(OttSettings.OttOperation.TRANSACTIONS);
        String apiTransactionsEndpoint = transactionsOperation.getUrl();

        BindingProperties binding = ottSettings.getBinding(OttSettings.OttBinding.MERCHANTPAYMENT);
        String url = binding.getUrl();

        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.POST + "&enableCORS=" +
                                    ottSettings.isCorsEnabled();

        from(consumerEndpoint)
                .id("receive-merchant-payment-request")
                .log("Request received")
                .streamCaching()
                .process(exchange -> {
                    exchange.setProperty("apikey", ottSettings.getApikey());
                    exchange.setProperty("apiTransactionsEndpoint", apiTransactionsEndpoint);
                    exchange.setProperty("transactionType", binding.getName());
                    exchange.setProperty("mainBody", exchange.getIn().getBody(String.class));
                })
                .unmarshal().json(JsonLibrary.Jackson, TransactionObject.class)
                .to("direct:conductTransaction")
        ;
    }
}
