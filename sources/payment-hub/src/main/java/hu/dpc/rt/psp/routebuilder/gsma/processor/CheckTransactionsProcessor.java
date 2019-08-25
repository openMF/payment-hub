package hu.dpc.rt.psp.routebuilder.gsma.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import hu.dpc.rt.psp.dto.gsma.TransactionObject;

import java.util.Arrays;

@Component("checkTransactionsProcessor")
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

        exchange.getOut().setBody(transactionObject);

        /*
        String responseUrl = exchange.getProperty("clientUrl", String.class);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(transactionObject.toString(), httpHeaders);

        HttpMethod httpMethod = HttpMethod.POST;

        TransactionObject response = restTemplate.exchange(responseUrl, httpMethod, entity,
                TransactionObject.class).getBody();

        System.out.println("Response to client: " + response.toString());
         */
    }
}
