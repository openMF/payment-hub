package hu.dpc.rt.psp.routebuilder.gsma.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import hu.dpc.rt.psp.dto.gsma.TransactionObject;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Component("postTransactions")
public class PostTransactionsProcessor implements Processor {

    RestTemplate restTemplate;

    public PostTransactionsProcessor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void process (Exchange exchange) throws Exception {

        //Message data = exchange.getIn();
        String body = exchange.getIn().getBody(String.class);
        //System.out.println(body);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        String corrId = UUID.randomUUID().toString();
        httpHeaders.set("X-CorrelationID", corrId);

        String date = LocalDateTime.now().toString();
        httpHeaders.set("Date", date);

        HttpEntity<String> entity = new HttpEntity<>(body, httpHeaders);
        //System.out.println("Entity: " + entity);

        HttpMethod httpMethod = HttpMethod.POST;

        String apikey = "u8YfSQNnNsGFAaqRm3sGShpO2ywLRJgs";

        String endpointUrl = "https://sandbox.mobilemoneyapi.io/simulator/v1.0/mm/transactions?apikey=" + apikey;

        TransactionObject response = restTemplate.exchange(endpointUrl, httpMethod, entity,
                                        TransactionObject.class).getBody();

        //System.out.println(response.toString());

        exchange.getIn().setBody(response);
    }
}
