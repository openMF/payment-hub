package hu.dpc.rt.psp.routebuilder.gsma.channel;

import hu.dpc.rt.psp.config.BindingProperties;
import hu.dpc.rt.psp.config.HubSettings;
import hu.dpc.rt.psp.config.OperationProperties;
import hu.dpc.rt.psp.config.OttSettings;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import hu.dpc.rt.psp.dto.gsma.TransactionObject;

@Configuration
public class PeerToPeerTransfer extends RouteBuilder {

    private HubSettings hubSettings;
    private OttSettings ottSettings;

    @Autowired
    public PeerToPeerTransfer (CamelContext camelContext, HubSettings hubSettings,
                               OttSettings ottSettings) {
        super(camelContext);
        this.hubSettings = hubSettings;
        this.ottSettings = ottSettings;
    }

    @Override
    public void configure() throws Exception {
        getContext().getShutdownStrategy().setTimeout(1);

        OperationProperties transactionsOperation = ottSettings.getOperation(OttSettings.OttOperation.TRANSACTIONS);
        String apiTransactionsEndpoint = transactionsOperation.getUrl();

        OperationProperties AccountsOperation = ottSettings.getOperation(OttSettings.OttOperation.ACCOUNTS);
        String apiAccountsEndpoint = AccountsOperation.getUrl();

        BindingProperties binding = ottSettings.getBinding(OttSettings.OttBinding.TRANSFER);
        String url = binding.getUrl();

        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.POST + "&enableCORS=" +
                                    ottSettings.isCorsEnabled();

        from(consumerEndpoint)
                .id("receive-transfer-request")
                .log("Request received")
                .streamCaching()
                .process(exchange -> {
                    exchange.setProperty("apikey", ottSettings.getApikey());
                    exchange.setProperty("apiTransactionsEndpoint", apiTransactionsEndpoint);
                    exchange.setProperty("apiAccountsEndpoint", apiAccountsEndpoint);
                    exchange.setProperty("transactionType", binding.getName());
                    exchange.setProperty("mainBody", exchange.getIn().getBody(String.class));
                })
                .unmarshal().json(JsonLibrary.Jackson, TransactionObject.class)
                .to("direct:conductTransaction")
        ;
    }
}
