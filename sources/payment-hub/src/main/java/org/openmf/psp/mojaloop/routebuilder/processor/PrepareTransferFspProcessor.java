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
import org.openmf.psp.dto.fsp.TransferFspRequestDTO;
import org.openmf.psp.dto.fsp.TransferFspResponseDTO;
import org.openmf.psp.mojaloop.cache.TransactionContextHolder;
import org.openmf.psp.mojaloop.constant.ExchangeHeader;
import org.openmf.psp.mojaloop.internal.TransactionCacheContext;
import org.openmf.psp.mojaloop.internal.TransactionRoleContext;
import org.openmf.psp.type.TransactionRole;
import org.openmf.psp.util.IbanUtil;
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
