package hu.dpc.rt.psp.routebuilder.gsma.channel;

import hu.dpc.rt.psp.config.OttSettings;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseTransaction extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:conductTransaction")
            .id("conductTransaction")
            .log("Starting transaction")
            .to("direct:checkTransactionRequest")
        ;

        from("direct:checkTransactionRequest")
                .id("checkTransactionRequest")
                .log("Checking transaction...")
                .process("checkTransactionsTypeProcessor")
                .choice()
                .when(exchange -> exchange.getIn().getBody(String.class).equals(""))
                    .log("Breaking...")
                .otherwise()
                    .to("direct:verifyTransactions")
                .end()
        ;

        from("direct:verifyTransactions")
                .id("verifyTransactions")
                .choice()
                .when(exchange -> exchange.getProperty("transactionType", String.class).equals(OttSettings.OttBinding.TRANSFER))
                    .to("direct:checkReceiverId")
                .otherwise()
                    .to("direct:commitTransaction")
        ;

        from("direct:checkReceiverId")
                .id("checkReceiverId")
                .process("checkReceiverAccountsProcessor")
                .choice()
                .when(exchange -> exchange.getIn().getBody() == null)
                    .log("Error: credit party does not exist")
                .otherwise()
                    .process(exchange -> {
                        exchange.getIn().setBody(exchange.getProperty("mainBody"));
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
                .choice()
                .when(exchange -> exchange.getProperty("isTransactionSuccess", boolean.class))
                    .log("Transaction completed")
                .otherwise()
                    .log("Transaction failed")
        ;
    }
}
