package org.openmf.psp.mpesa.routebuilder.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmf.psp.mpesa.dto.TransactionResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("postTransactionProcessor")
public class PostTransactionProcessor implements Processor {

    RestTemplate restTemplate;

    public PostTransactionProcessor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void process (Exchange exchange) throws Exception {

        String body = exchange.getProperty("mainBody", String.class);

        String url = exchange.getProperty("apiTransactionEndpoint", String.class);
        String access_token = exchange.getProperty("access_token", String.class);

        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", "Bearer " + access_token);
        headers.set("content-type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        HttpMethod method = HttpMethod.POST;

        ResponseEntity<TransactionResponse> responseEntity = restTemplate.exchange(url, method, entity, TransactionResponse.class);
        TransactionResponse response = responseEntity.getBody();

        if (responseEntity.getStatusCode() == HttpStatus.OK && response != null) {
            exchange.setProperty("transactionResponseCode", "200");
            exchange.getIn().setBody(response);
        } else {
            exchange.setProperty("transactionResponseCode", String.valueOf(responseEntity.getStatusCode().value()));
        }
    }

}
