/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.routebuilder.processor;

import hu.dpc.rt.psp.cache.TransactionContextHolder;
import hu.dpc.rt.psp.component.IlpBuilderDpc;
import hu.dpc.rt.psp.component.SwitchRestClient;
import hu.dpc.rt.psp.constant.ExchangeHeader;
import hu.dpc.rt.psp.dto.MoneyData;
import hu.dpc.rt.psp.dto.mojaloop.QuoteSwitchResponseDTO;
import hu.dpc.rt.psp.internal.Ilp;
import hu.dpc.rt.psp.internal.TransactionCacheContext;
import hu.dpc.rt.psp.internal.TransactionRoleContext;
import hu.dpc.rt.psp.type.AmountType;
import hu.dpc.rt.psp.type.TransactionRole;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Processor to send PUT /quotes response through the switch.
 */
@Component("quotesSwitchResponseProcessor")
public class QuotesSwitchResponseProcessor implements Processor {

    private TransactionContextHolder transactionContextHolder;

    private SwitchRestClient switchRestClient;

    private IlpBuilderDpc ilpBuilderDpc;

    @Autowired
    public QuotesSwitchResponseProcessor(TransactionContextHolder transactionContextHolder, SwitchRestClient switchRestClient,
                                         IlpBuilderDpc ilpBuilderDpc) {
        this.transactionContextHolder = transactionContextHolder;
        this.switchRestClient = switchRestClient;
        this.ilpBuilderDpc = ilpBuilderDpc;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String transactionId = exchange.getProperty(ExchangeHeader.TRANSACTION_ID.getKey(), String.class);
        TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);

        TransactionRoleContext payeeContext = transactionContext.getRoleContext(TransactionRole.PAYEE);
        TransactionRoleContext payerContext = transactionContext.getRoleContext(TransactionRole.PAYER);

        BigDecimal transactionAmount = transactionContext.getTransactionAmount();
        BigDecimal transferAmount = transactionContext.getTransferAmount();
        MoneyData fee = payeeContext.getFee();
        MoneyData commission = payeeContext.getCommission();

        BigDecimal payeeReceiveAmount = transactionContext.getAmountType() == AmountType.RECEIVE
                ? transactionAmount
                : transferAmount.add(commission.getAmountDecimal()).subtract(fee.getAmountDecimal());

        String currency = transactionContext.getCurrency();
        Ilp ilp = ilpBuilderDpc.build(transactionId, transactionContext.getQuoteId(), transactionAmount, currency,
                payerContext.getPartyContext().getParty(), payeeContext.getPartyContext().getParty(), transferAmount);
        transactionContextHolder.registerIlp(transactionId, ilp);

        MoneyData transferData = new MoneyData(transferAmount, currency);
        MoneyData receiveData = new MoneyData(payeeReceiveAmount, currency);
        QuoteSwitchResponseDTO response = new QuoteSwitchResponseDTO(transferData, receiveData, fee, commission, transactionContext.getExpiration(),
                transactionContext.getGeoCode(), ilp.getPacket(), ilp.getCondition(), transactionContext.getExtensionList());

        switchRestClient.callPutQuotes(response, transactionContext.getQuoteId(), payeeContext.getFspId(), payerContext.getFspId());
    }
}
