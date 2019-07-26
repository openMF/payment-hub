package hu.dpc.rt.psp.routebuilder.gsma.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import hu.dpc.rt.psp.dto.gsma.TransactionObject;

@Component("confirmTransactions")
public class CheckTransactionsProcessor implements Processor {

    RestTemplate restTemplate;
    Logger logger = LoggerFactory.getLogger(CheckTransactionsProcessor.class);

    public CheckTransactionsProcessor (RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    @Override
    public void process (Exchange exchange) throws Exception {

        Message in = exchange.getIn();
        TransactionObject transactionObject = in.getBody(TransactionObject.class);
        //System.out.println(transactionObject.toString());

        if (transactionObject.getTransactionStatus().equals("completed")) {
            logger.debug("Transaction completed.");
        }
    }

}
