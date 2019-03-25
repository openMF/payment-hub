/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.routebuilder.processor;

import hu.dpc.rt.psp.cache.TransactionContextHolder;
import hu.dpc.rt.psp.component.FspRestClient;
import hu.dpc.rt.psp.constant.ExchangeHeader;
import hu.dpc.rt.psp.dto.FspMoneyData;
import hu.dpc.rt.psp.dto.MoneyData;
import hu.dpc.rt.psp.dto.fsp.TransferFspRequestDTO;
import hu.dpc.rt.psp.dto.fsp.TransferFspResponseDTO;
import hu.dpc.rt.psp.internal.TransactionCacheContext;
import hu.dpc.rt.psp.internal.TransactionRoleContext;
import hu.dpc.rt.psp.type.TransactionRole;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
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
