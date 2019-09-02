package hu.dpc.rt.psp.routebuilder.gsma.processor;

import hu.dpc.rt.psp.dto.gsma.TransactionObject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("checkTransactionsTypeProcessor")
public class CheckTransactionsTypeProcessor implements Processor {

    RestTemplate restTemplate;

    public CheckTransactionsTypeProcessor (RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void process (Exchange exchange) throws Exception {

        Message in = exchange.getIn();
        TransactionObject transactionObject = in.getBody(TransactionObject.class);

        //TODO: add more checks

        String transactionType = exchange.getProperty("transactionType", String.class);

        if (!(transactionType.equals(transactionObject.getType()))) {
            System.out.println("Transaction type does not match.");
            exchange.getIn().setBody(null);
        }
    }
}
