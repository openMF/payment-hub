/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */

/**
@Author Sidhant Gupta
*/
package org.openmf.psp.gsma.routebuilder.processor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmf.psp.gsma.dto.GSMATransaction;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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

        GSMATransaction response = restTemplate.exchange(endpointUrl, httpMethod, entity,
        		GSMATransaction.class).getBody();

        exchange.getIn().setBody(response);
    }
}