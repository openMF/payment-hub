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
import org.openmf.psp.mojaloop.cache.TransactionContextHolder;
import org.openmf.psp.mojaloop.component.SwitchRestClient;
import org.openmf.psp.mojaloop.constant.ExchangeHeader;
import org.openmf.psp.mojaloop.dto.mojaloop.TransferSwitchResponseDTO;
import org.openmf.psp.mojaloop.internal.TransactionCacheContext;
import org.openmf.psp.mojaloop.internal.TransactionRoleContext;
import org.openmf.psp.mojaloop.type.TransferState;
import org.openmf.psp.type.TransactionRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Processor to send PUT /transfers COMMIT response through the switch.
 */
@Component("transfersSwitchResponseProcessor")
public class TransfersSwitchResponseProcessor implements Processor {

    private TransactionContextHolder transactionContextHolder;

    private SwitchRestClient switchRestClient;

    @Autowired
    public TransfersSwitchResponseProcessor(TransactionContextHolder transactionContextHolder, SwitchRestClient switchRestClient) {
        this.transactionContextHolder = transactionContextHolder;
        this.switchRestClient = switchRestClient;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String transactionId = exchange.getProperty(ExchangeHeader.TRANSACTION_ID.getKey(), String.class);
        TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);

        TransactionRoleContext payeeContext = transactionContext.getRoleContext(TransactionRole.PAYEE);
        TransactionRoleContext payerContext = transactionContext.getRoleContext(TransactionRole.PAYER);


        LocalDateTime localDateTime = LocalDateTime.now();
        TransferSwitchResponseDTO response = new TransferSwitchResponseDTO(transactionContext.getIlp().getFulfilment(),
                localDateTime, TransferState.COMMITTED, transactionContext.getExtensionList());

        switchRestClient.callPutTransferCommit(response, transactionContext.getTransferId(), payeeContext.getFspId(), payerContext.getFspId());
    }
}
