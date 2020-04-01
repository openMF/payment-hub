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

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.openmf.psp.gsma.dto.GSMATransaction;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("checkTransactionProcessor")
public class CheckTransactionProcessor implements Processor {

    RestTemplate restTemplate;

    public CheckTransactionProcessor (RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    @Override
    public void process (Exchange exchange) throws Exception {

        Message in = exchange.getIn();
        GSMATransaction transactionObject = in.getBody(GSMATransaction.class);

        if (transactionObject.getTransactionStatus().equals("completed")) {
            exchange.setProperty("isTransactionSuccess", true);
        }

        exchange.getOut().setBody(transactionObject);
    }
}
