package org.openmf.psp.mpesa.routebuilder.channel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.openmf.psp.config.BindingProperties;
import org.openmf.psp.config.HubSettings;
import org.openmf.psp.config.OperationProperties;
import org.openmf.psp.mpesa.config.MPesaSettings;
import org.openmf.psp.mpesa.dto.BalanceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountBalance extends RouteBuilder {

    private HubSettings hubSettings;
    private MPesaSettings mPesaSettings;

    @Autowired
    public AccountBalance(CamelContext camelContext, HubSettings hubSettings, MPesaSettings mPesaSetting) {
        super(camelContext);
        this.hubSettings = hubSettings;
        this.mPesaSettings = mPesaSetting;
    }

    @Override
    public void configure() throws Exception {

        OperationProperties transactionOperation = mPesaSettings.getOperation(MPesaSettings.MPesaOperation.BALANCE);
        OperationProperties oauthOperation = mPesaSettings.getOperation(MPesaSettings.MPesaOperation.OAUTH);
        String apiTransactionEndpoint = transactionOperation.getUrl();
        String apiOAuthEndpoint = oauthOperation.getUrl();

        BindingProperties binding = mPesaSettings.getBinding(MPesaSettings.MPesaBinding.BALANCE);
        String url = binding.getUrl();

        String consumerEndpoint = ""; //TODO: Add consumerEndpoint after discussion

        from(consumerEndpoint)
                .id("receive-account-balance-check-request")
                .log("Account balance check request received")
                .streamCaching()
                .process(exchange -> {
                    exchange.setProperty("consumerKey", mPesaSettings.getConsumerKey());
                    exchange.setProperty("consumerSecret", mPesaSettings.getConsumerSecret());
                    exchange.setProperty("apiOAuthEndpoint", apiOAuthEndpoint);
                    exchange.setProperty("apiTransactionEndpoint", apiTransactionEndpoint);
                    exchange.setProperty("transactionType", binding.getName());
                    exchange.setProperty("mainBody", exchange.getIn().getBody(String.class));
                })

                .unmarshal().json(JsonLibrary.Jackson, BalanceRequest.class)
                .to("direct:conductTransaction")
        ;

    }
}
