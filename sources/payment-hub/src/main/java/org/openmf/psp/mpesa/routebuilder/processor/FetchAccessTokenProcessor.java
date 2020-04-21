package org.openmf.psp.mpesa.routebuilder.processor;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.nio.charset.StandardCharsets;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmf.psp.mpesa.dto.AccessTokenResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("fetchAccessTokenProcessor")
public class FetchAccessTokenProcessor implements Processor {

    RestTemplate restTemplate;

    public FetchAccessTokenProcessor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void process (Exchange exchange) throws Exception {

        String app_key = exchange.getProperty("consumerKey", String.class);
        String app_secret = exchange.getProperty("consumerSecret", String.class);
        String appKeySecret = app_key + ":" + app_secret;
        byte[] bytes = appKeySecret.getBytes(StandardCharsets.ISO_8859_1);
        String auth = Base64.encode(bytes);

        String url = exchange.getProperty("apiOAuthEndpoint", String.class);

        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", "Basic " + auth);
        headers.set("cache-control", "no-cache");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        HttpMethod method = HttpMethod.GET;

        ResponseEntity<AccessTokenResponse> responseEntity = restTemplate.exchange(url, method, entity, AccessTokenResponse.class);
        AccessTokenResponse response = responseEntity.getBody();

        if (responseEntity.getStatusCode() == HttpStatus.OK && response != null) {
            exchange.setProperty("tokenResponseCode", "200");
            exchange.setProperty("access_token", response.getAccess_token());
        } else {
            exchange.setProperty("tokenResponseCode", String.valueOf(responseEntity.getStatusCode().value()));
        }

    }

}
