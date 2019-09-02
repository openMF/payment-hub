package hu.dpc.rt.psp.routebuilder.gsma.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import hu.dpc.rt.psp.dto.gsma.TransactionObject;

@Component("checkTransactionsProcessor")
public class CheckTransactionsProcessor implements Processor {

    RestTemplate restTemplate;

    public CheckTransactionsProcessor (RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    @Override
    public void process (Exchange exchange) throws Exception {

        Message in = exchange.getIn();
        TransactionObject transactionObject = in.getBody(TransactionObject.class);

        if (transactionObject.getTransactionStatus().equals("completed")) {
            exchange.setProperty("isTransactionSuccess", true);
        }

        exchange.getOut().setBody(transactionObject);
    }
}
