/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.routebuilder.mojaloop.processor;

import hu.dpc.rt.psp.cache.TransactionContextHolder;
import hu.dpc.rt.psp.component.SwitchRestClient;
import hu.dpc.rt.psp.constant.ExchangeHeader;
import hu.dpc.rt.psp.dto.mojaloop.TransferSwitchResponseDTO;
import hu.dpc.rt.psp.internal.TransactionCacheContext;
import hu.dpc.rt.psp.internal.TransactionRoleContext;
import hu.dpc.rt.psp.type.TransactionRole;
import hu.dpc.rt.psp.type.TransferState;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
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


        LocalDateTime localDateTime = ZonedDateTime.parse("2019-02-19T14:27:52.827Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
        TransferSwitchResponseDTO response = new TransferSwitchResponseDTO(transactionContext.getIlp().getFulfilment(),
                localDateTime, TransferState.COMMITTED, transactionContext.getExtensionList());

        switchRestClient.callPutTransferCommit(response, transactionContext.getTransferId(), payeeContext.getFspId(), payerContext.getFspId());
    }
}
