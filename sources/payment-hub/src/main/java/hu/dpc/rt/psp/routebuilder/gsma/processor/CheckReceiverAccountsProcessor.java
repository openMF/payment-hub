package hu.dpc.rt.psp.routebuilder.gsma.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import hu.dpc.rt.psp.dto.gsma.AccountStatus;
import hu.dpc.rt.psp.dto.gsma.Party;
import hu.dpc.rt.psp.dto.gsma.TransactionObject;

import java.time.LocalDateTime;

@Component("checkReceiverAccountsProcessor")
public class CheckReceiverAccountsProcessor implements Processor {

    RestTemplate restTemplate;

    public CheckReceiverAccountsProcessor (RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    @Override
    public void process (Exchange exchange) throws Exception {

         Message in = exchange.getIn();
         TransactionObject transactionObject = in.getBody(TransactionObject.class);

         for (Party p: transactionObject.getCreditParty()) {
             String key = p.getKey();
             String value = p.getValue();

             HttpHeaders httpHeaders = new HttpHeaders();

             String date = LocalDateTime.now().toString();
             httpHeaders.set("Date", date);

             String body = null;
             HttpEntity<String> entity = new HttpEntity<>(body, httpHeaders);

             HttpMethod httpMethod = HttpMethod.GET;

             String apikey = exchange.getProperty("apikey", String.class);

             //String endpointUrl = exchange.getProperty("apiEndpoint", String.class) + "?apikey=" + apikey;

             String endpointUrl = exchange.getProperty("apiAccountsEndpoint", String.class) + key + "@" +
                                    value + "/status" + "?apikey=" + apikey;

             //System.out.println(key + value);

             AccountStatus response = restTemplate.exchange(endpointUrl, httpMethod, entity,
                                        AccountStatus.class).getBody();

             if (response.getStatus().equals("available") != true) {

                 System.out.println("Account does not exist, breaking");
                 exchange.getIn().setBody(null);
             }
         }
    }
}
