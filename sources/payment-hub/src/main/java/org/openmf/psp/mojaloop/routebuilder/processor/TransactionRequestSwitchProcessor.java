/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.mojaloop.routebuilder.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmf.psp.config.SwitchSettings;
import org.openmf.psp.mojaloop.cache.TransactionContextHolder;
import org.openmf.psp.mojaloop.component.SwitchRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Processor to send POST /transactionRequests request to the PAYER FSP through interoperable switch.
 */
@Component("transactionRequestSwitchProcessor")
public class TransactionRequestSwitchProcessor implements Processor {

    private TransactionContextHolder transactionContextHolder;

    private SwitchSettings switchSettings;
    private SwitchRestClient switchRestClient;

    @Autowired
    public TransactionRequestSwitchProcessor(TransactionContextHolder transactionContextHolder, SwitchSettings switchSettings,
                                             SwitchRestClient switchRestClient) {
        this.transactionContextHolder = transactionContextHolder;
        this.switchSettings = switchSettings;
        this.switchRestClient = switchRestClient;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        // add transaction id to the extension list EXTENSION_KEY_TRANSACTION_ID
        throw new RuntimeException("Call /transactionRequest is not supported yet");
    }
}
