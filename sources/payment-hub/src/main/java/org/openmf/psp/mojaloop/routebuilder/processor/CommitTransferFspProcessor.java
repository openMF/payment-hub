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
import org.openmf.psp.component.FspRestClient;
import org.openmf.psp.dto.FspMoneyData;
import org.openmf.psp.dto.MoneyData;
import org.openmf.psp.dto.fsp.TransferFspRequestDTO;
import org.openmf.psp.dto.fsp.TransferFspResponseDTO;
import org.openmf.psp.mojaloop.cache.TransactionContextHolder;
import org.openmf.psp.mojaloop.constant.ExchangeHeader;
import org.openmf.psp.mojaloop.internal.TransactionCacheContext;
import org.openmf.psp.mojaloop.internal.TransactionRoleContext;
import org.openmf.psp.type.TransactionRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Processor to send POST /transfers COMMIT request to FSP.
 */
@Component("commitTransferFspProcessor")
public class CommitTransferFspProcessor implements Processor {

    private FspRestClient fspRestClient;

    private TransactionContextHolder transactionContextHolder;

    @Autowired
    public CommitTransferFspProcessor(FspRestClient fspRestClient, TransactionContextHolder transactionContextHolder) {
        this.fspRestClient = fspRestClient;
        this.transactionContextHolder = transactionContextHolder;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String transactionId = exchange.getProperty(ExchangeHeader.TRANSACTION_ID.getKey(), String.class);
        TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);

        TransactionRole transferRole = exchange.getProperty(ExchangeHeader.TRANSFER_ROLE.getKey(), TransactionRole.class);
        if (transferRole == null)
            transferRole = exchange.getProperty(ExchangeHeader.CURRENT_ROLE.getKey(), TransactionRole.class);

        TransactionRoleContext roleContext = transactionContext.getRoleContext(transferRole);
        String accountId = roleContext.getPartyContext().getAccountId();

        String transferId = transactionContext.getOrCreateTransferId();

        FspMoneyData amount = new FspMoneyData(transactionContext.getTransferAmount(), transactionContext.getCurrency());
        TransferFspRequestDTO transferRequestDTO = new TransferFspRequestDTO(transactionId, transferId, accountId, amount,
                MoneyData.toFspMoneyData(roleContext.getFee()), MoneyData.toFspMoneyData(roleContext.getCommission()),
                transferRole, transactionContext.getTransactionType(), transactionContext.getNote());

        TransferFspResponseDTO transferResponseDTO = fspRestClient.callTransferCommit(transferRequestDTO, roleContext.getFspId());

        transactionContextHolder.updateFspCommitTransfer(transactionId, transferRole, transferResponseDTO);

    }
}
