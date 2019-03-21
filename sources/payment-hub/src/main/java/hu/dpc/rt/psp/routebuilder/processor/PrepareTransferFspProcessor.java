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
import hu.dpc.rt.psp.dto.fsp.TransferFspRequestDTO;
import hu.dpc.rt.psp.dto.fsp.TransferFspResponseDTO;
import hu.dpc.rt.psp.internal.TransactionCacheContext;
import hu.dpc.rt.psp.internal.TransactionRoleContext;
import hu.dpc.rt.psp.type.TransactionRole;
import hu.dpc.rt.psp.util.IbanUtil;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Processor to send POST /transfers PREPARE request to FSP.
 */
@Component("prepareTransferFspProcessor")
public class PrepareTransferFspProcessor implements Processor {

    private FspRestClient fspRestClient;

    private TransactionContextHolder transactionContextHolder;

    private IbanUtil ibanUtil;

    PrepareTransferFspProcessor() {
    }

    @Autowired
    public PrepareTransferFspProcessor(FspRestClient fspRestClient, TransactionContextHolder transactionContextHolder,
                                       IbanUtil ibanUtil) {
        this.fspRestClient = fspRestClient;
        this.transactionContextHolder = transactionContextHolder;
        this.ibanUtil = ibanUtil;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String transactionId = exchange.getProperty(ExchangeHeader.TRANSACTION_ID.getKey(), String.class);
        TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);

        TransactionRole currentRole = exchange.getProperty(ExchangeHeader.CURRENT_ROLE.getKey(), TransactionRole.class);
        TransactionRoleContext roleContext = transactionContext.getRoleContext(currentRole);
        String accountId = roleContext.getPartyContext().getAccountId();

        String transferId = transactionContext.getOrCreateTransferId();

        FspMoneyData amount = new FspMoneyData(transactionContext.getTransferAmount(), transactionContext.getCurrency());
        TransferFspRequestDTO transferRequestDTO = new TransferFspRequestDTO(transactionId, transferId, accountId, amount, roleContext.getFspQuoteDTO().getFspFee(),
                roleContext.getFspQuoteDTO().getFspCommission(), currentRole, transactionContext.getTransactionType(), transactionContext.getNote());

        TransferFspResponseDTO transferResponseDTO = fspRestClient.callPrepareTransfer(transferRequestDTO, roleContext.getFspId());

        transactionContextHolder.updateFspPrepareTransfer(transactionId, currentRole, transferResponseDTO);
    }
}
