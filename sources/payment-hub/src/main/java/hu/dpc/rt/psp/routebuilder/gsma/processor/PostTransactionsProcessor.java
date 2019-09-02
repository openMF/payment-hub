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

@Component("postTransactionsProcessor")
public class PostTransactionsProcessor implements Processor {

    RestTemplate restTemplate;

    public PostTransactionsProcessor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void process (Exchange exchange) throws Exception {

        String body = exchange.getProperty("mainBody", String.class);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        String corrId = UUID.randomUUID().toString();
        httpHeaders.set("X-CorrelationID", corrId);

        String date = LocalDateTime.now().toString();
        httpHeaders.set("Date", date);

        HttpEntity<String> entity = new HttpEntity<>(body, httpHeaders);

        HttpMethod httpMethod = HttpMethod.POST;

        String apikey = exchange.getProperty("apikey", String.class);

        String endpointUrl = exchange.getProperty("apiTransactionsEndpoint", String.class) + "?apikey=" + apikey;

        TransactionObject response = restTemplate.exchange(endpointUrl, httpMethod, entity,
                                        TransactionObject.class).getBody();

        exchange.getIn().setBody(response);
    }
}
