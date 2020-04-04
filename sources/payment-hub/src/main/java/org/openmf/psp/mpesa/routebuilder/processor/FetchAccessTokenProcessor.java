package org.openmf.psp.mpesa.routebuilder.processor;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmf.psp.mpesa.dto.AccessTokenResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
        byte[] bytes = appKeySecret.getBytes("ISO-8859-1");
        String auth = Base64.encode(bytes);

        String url = exchange.getProperty("apiOAuthEndpoint", String.class);

        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", "Basic " + auth);
        headers.set("cache-control", "no-cache");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        HttpMethod method = HttpMethod.GET;

        HttpStatus responseCode = restTemplate.exchange(url, method, entity, AccessTokenResponse.class).getStatusCode();

        AccessTokenResponse response = null;

        if (responseCode == HttpStatus.OK) {
            response = restTemplate.exchange(url, method, entity, AccessTokenResponse.class).getBody();
            exchange.setProperty("tokenResponseCose", "200");
            exchange.setProperty("access_token", response.getAccess_token());
        } else {
            exchange.setProperty("tokenResponseCode", String.valueOf(responseCode.value()));
        }

    }

}
