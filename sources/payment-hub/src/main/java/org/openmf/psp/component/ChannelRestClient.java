/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.component;

import org.openmf.psp.config.ChannelSettings;
import org.openmf.psp.config.TenantProperties;
import org.openmf.psp.dto.channel.TransactionChannelAsyncResponseDTO;
import org.openmf.psp.internal.FspId;
import org.openmf.psp.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class ChannelRestClient {

    private static Logger logger = LoggerFactory.getLogger(ChannelRestClient.class);

    private RestClient restClient;

    private ChannelSettings channelSettings;


    @Autowired
    public ChannelRestClient(RestClient restClient, ChannelSettings channelSettings) {
        this.restClient = restClient;
        this.channelSettings = channelSettings;
    }

    @PostConstruct
    public void postConstruct() {
    }

    public void callPaymentAsyncResponse(TransactionChannelAsyncResponseDTO response, FspId fspId) {
        String tenant = fspId.getTenant();
        TenantProperties operation = channelSettings.getOperation(ChannelSettings.ChannelOperation.NOTIFICATION_TRANSFERS, tenant);

        String url = operation.getUrl() + '/' + response.getTransactionId();

        Map<String, String> headers = getHeaders(tenant);
        logger.debug(String.format("Sending request %s, method: %s, \nheader-keys: %s, \nheader-values: %s, \nbody: %s, ",
                url, HttpMethod.PUT, Arrays.toString(headers.keySet().toArray()), Arrays.toString(headers.values().toArray()),
                JsonUtil.toJson(response)));

//        restClient.call(url, HttpMethod.PUT, headers, JsonUtil.toJson(response));
    }

    private Map<String, String> getHeaders(String tenantId) {
        Map<String, String> headers = new HashMap<>();

        String tenantHeader = channelSettings.getHeader(ChannelSettings.ChannelHeader.TENANT).getKey();
        headers.put(tenantHeader, tenantId);
        return headers;
    }
}
