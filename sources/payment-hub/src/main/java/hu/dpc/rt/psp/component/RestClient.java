/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import hu.dpc.rt.psp.util.RestTemplateHeaderModifierInterceptor;

@Component
public class RestClient {

    private static Logger logger = LoggerFactory.getLogger(RestClient.class);

    static final DateTimeFormatter HEADER_DATE_TIME_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME;

    private RestTemplate restTemplate;

    @Autowired
    public RestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String call(String endpointUrl, HttpMethod httpMethod, Map<String, String> headers, String body) {
        return call(endpointUrl, httpMethod, headers, body, true);
    }

    public String call(String endpointUrl, HttpMethod httpMethod, Map<String, String> headers, String body, boolean setAccept) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (setAccept) {
            httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        } else {
            // The quoting service will throw an error if the 'accept' or 'accept-charset' headers are set
            // RestTemplate sets accept-charset, accept, and accept-encoding by default, so get rid of them
            restTemplate.setInterceptors(Arrays.asList(new RestTemplateHeaderModifierInterceptor()));
        }

        headers.forEach((key, value) -> httpHeaders.set(key, value));

        HttpEntity<String> entity = new HttpEntity<>(body, httpHeaders);

        logger.debug(String.format("Sending request %s, method: %s, \nheader-keys: %s, \nheader-values: %s, \nbody: %s, ", endpointUrl, httpMethod, Arrays.toString(httpHeaders.keySet().toArray()), Arrays.toString(httpHeaders.values().toArray()), body));

        String res = restTemplate.exchange(endpointUrl, httpMethod, entity, String.class).getBody();

        if (!setAccept) {
            // Reset so that we aren't messing with headers after this request
            List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
            restTemplate.setInterceptors(interceptors);
        }

        return res;
    }

    public static String formatHeaderDate(ZonedDateTime date) {
        return date == null ? null : date.format(HEADER_DATE_TIME_FORMATTER);
    }

    public static String formatServerHeaderDate() {
        return formatHeaderDate(ZonedDateTime.now(ZoneId.of("UTC")));
    }
}
