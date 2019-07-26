package hu.dpc.rt.psp.routebuilder.gsma.channel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import hu.dpc.rt.psp.dto.gsma.TransactionObject;

@Configuration
public class PeerToPeerTransfer extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        String port = "8081";
        String url = "http://0.0.0.0:" + port + "/transfer";

        String clientUrl = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.POST;

        from(clientUrl)
                .id("receiveTransferRequest")
                .log("Checking receiver account(s)...")
                .streamCaching()
                .process(exchange -> {
                    exchange.setProperty("mainBody", exchange.getIn().getBody(String.class));
                })
                .unmarshal().json(JsonLibrary.Jackson, TransactionObject.class)
                .to("direct:checkReceiverId")
        ;

        from("direct:checkReceiverId")
                .id("checkReceiverId")
                .process("checkReceiverAccounts")
                .choice()
                .when(exchange -> exchange.getIn().getBody() == null)
                    .log("Error: credit party does not exist")
                .otherwise()
                    .process(exchange -> {
                        exchange.getIn().setBody(exchange.getProperty("mainBody"));
                    })
                    .to("direct:commitTransaction")
                .end()
        ;
    }
}
