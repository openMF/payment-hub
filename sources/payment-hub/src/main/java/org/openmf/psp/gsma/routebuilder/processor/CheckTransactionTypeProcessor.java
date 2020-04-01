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

@Component("checkTransactionTypeProcessor")
public class CheckTransactionTypeProcessor implements Processor {

    RestTemplate restTemplate;

    public CheckTransactionTypeProcessor (RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void process (Exchange exchange) throws Exception {

        Message in = exchange.getIn();
        GSMATransaction transactionObject = in.getBody(GSMATransaction.class);

        //TODO: add more checks

        String transactionType = exchange.getProperty("transactionType", String.class);

        if (!(transactionType.equals(transactionObject.getType()))) {
            System.out.println("Transaction type does not match.");
            exchange.getIn().setBody(null);
        }
    }
}
