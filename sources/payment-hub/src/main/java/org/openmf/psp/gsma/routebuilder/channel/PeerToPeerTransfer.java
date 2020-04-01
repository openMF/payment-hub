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
package org.openmf.psp.gsma.routebuilder.channel;

import javax.ws.rs.HttpMethod;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.openmf.psp.config.BindingProperties;
import org.openmf.psp.config.HubSettings;
import org.openmf.psp.config.OperationProperties;
import org.openmf.psp.gsma.config.GSMASettings;
import org.openmf.psp.gsma.dto.GSMATransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PeerToPeerTransfer extends RouteBuilder {

    private HubSettings hubSettings;
    private GSMASettings ottSettings;

    @Autowired
    public PeerToPeerTransfer (CamelContext camelContext, HubSettings hubSettings,
                               GSMASettings ottSettings) {
        super(camelContext);
        this.hubSettings = hubSettings;
        this.ottSettings = ottSettings;
    }

    @Override
    public void configure() throws Exception {
        getContext().getShutdownStrategy().setTimeout(1);

        OperationProperties transactionsOperation = ottSettings.getOperation(GSMASettings.OttOperation.TRANSACTIONS);
        String apiTransactionsEndpoint = transactionsOperation.getUrl();

        OperationProperties AccountsOperation = ottSettings.getOperation(GSMASettings.OttOperation.ACCOUNTS);
        String apiAccountsEndpoint = AccountsOperation.getUrl();

        BindingProperties binding = ottSettings.getBinding(GSMASettings.OttBinding.TRANSFER);
        String url = binding.getUrl();

        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.POST + "&enableCORS=" +
                                    ottSettings.isCorsEnabled();

        from(consumerEndpoint)
                .id("receive-transfer-request")
                .log("Request received")
                .streamCaching()
                .process(exchange -> {
                    exchange.setProperty("apikey", ottSettings.getApikey());
                    exchange.setProperty("apiTransactionsEndpoint", apiTransactionsEndpoint);
                    exchange.setProperty("apiAccountsEndpoint", apiAccountsEndpoint);
                    exchange.setProperty("transactionType", binding.getName());
                    exchange.setProperty("mainBody", exchange.getIn().getBody(String.class));
                })
                .unmarshal().json(JsonLibrary.Jackson, GSMATransaction.class)
                .to("direct:conductTransaction")
        ;
    }
}
