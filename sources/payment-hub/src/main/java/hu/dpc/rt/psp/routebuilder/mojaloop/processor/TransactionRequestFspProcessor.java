/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.routebuilder.mojaloop.processor;

import hu.dpc.rt.psp.cache.TransactionContextHolder;
import hu.dpc.rt.psp.component.FspRestClient;
import hu.dpc.rt.psp.constant.ExchangeHeader;
import hu.dpc.rt.psp.dto.FspMoneyData;
import hu.dpc.rt.psp.dto.fsp.TransactionRequestFspRequestDTO;
import hu.dpc.rt.psp.dto.fsp.TransactionRequestFspResponseDTO;
import hu.dpc.rt.psp.internal.TransactionCacheContext;
import hu.dpc.rt.psp.internal.TransactionRoleContext;
import hu.dpc.rt.psp.type.TransactionRole;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Processor to send POST /transactionRequests request to FSP.
 */
@Component("transactionRequestFspProcessor")
public class TransactionRequestFspProcessor implements Processor {

    private TransactionContextHolder transactionContextHolder;

    private FspRestClient fspRestClient;

    @Autowired
    public TransactionRequestFspProcessor(TransactionContextHolder transactionContextHolder, FspRestClient fspRestClient) {
        this.transactionContextHolder = transactionContextHolder;
        this.fspRestClient = fspRestClient;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String transactionId = exchange.getProperty(ExchangeHeader.TRANSACTION_ID.getKey(), String.class);
        TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);

        TransactionRole currentRole = TransactionRole.PAYER;
        TransactionRoleContext roleContext = transactionContext.getRoleContext(currentRole);
        String accountId = roleContext.getPartyContext().getAccountId();

        String requestId = transactionContext.getOrCreateTransactionRequestId();

        FspMoneyData amount = new FspMoneyData(transactionContext.getTransactionAmount(), transactionContext.getCurrency());
        TransactionRequestFspRequestDTO request = new TransactionRequestFspRequestDTO(transactionId, requestId, accountId,
                amount, currentRole, transactionContext.getTransactionType(), transactionContext.getNote(),
                transactionContext.getGeoCode(), transactionContext.getExpiration(), transactionContext.getExtensionList());

        TransactionRequestFspResponseDTO responseDTO = fspRestClient.callTransactionRequest(request, roleContext.getFspId());

        transactionContextHolder.updateFspTransactionRequest(transactionId, currentRole, responseDTO);
    }
}
