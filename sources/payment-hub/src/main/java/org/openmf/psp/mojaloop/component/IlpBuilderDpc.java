/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.mojaloop.component;

import com.ilp.conditions.models.pdp.Money;
import com.ilp.conditions.models.pdp.Transaction;

import org.openmf.psp.config.FspSettings;
import org.openmf.psp.config.HubSettings;
import org.openmf.psp.mojaloop.dto.mojaloop.QuoteSwitchRequestDTO;
import org.openmf.psp.mojaloop.internal.Ilp;
import org.openmf.psp.util.ContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;

@Component
public class IlpBuilderDpc {

    private static final String ILP_ADDRESS_TEMPLATE = "g.tz.%s.%s.%s";

    private IlpConditionHandlerImplDpc ilpConditionHandlerImplDpc = new IlpConditionHandlerImplDpc();
    private byte[] ilpSecret;

    private FspSettings fspSettings;
    private HubSettings hubSettings;

    @Autowired
    public IlpBuilderDpc(FspSettings fspSettings, HubSettings hubSettings) {
        this.fspSettings = fspSettings;
        this.hubSettings = hubSettings;
    }

    @PostConstruct
    public void postConstruct() {
        if (StringUtils.isEmpty(fspSettings.getIlpSecret())) {
            throw new RuntimeException(String.format("The ILP secret is not configured for FSP instance (%s)", hubSettings.getInstance()));
        }
        ilpSecret = fspSettings.getIlpSecret().getBytes();
    }

    public Ilp build(String transactionId, String quoteId, BigDecimal transactionAmount, String currency, org.openmf.psp.dto.Party payer,
                     org.openmf.psp.dto.Party payee, BigDecimal transferAmount) throws IOException {
        Transaction transaction = mapToTransaction(transactionId, quoteId, transactionAmount, currency, payer, payee);
        return build(transaction, transferAmount);
    }

    public Ilp build(Transaction transaction, BigDecimal amount) throws IOException {
        String ilpAddress = buildIlpAddress(transaction);
        String ilpPacket = ilpConditionHandlerImplDpc.getILPPacket(ilpAddress, ContextUtil.formatAmount(amount), transaction);

        String ilpCondition = ilpConditionHandlerImplDpc.generateCondition(ilpPacket, ilpSecret);

        String fulfillment = ilpConditionHandlerImplDpc.generateFulfillment(ilpPacket, ilpSecret);

        return new Ilp(ilpPacket, ilpCondition, fulfillment, transaction);
    }

    public Ilp parse(String packet, String condition) throws IOException {
        return new Ilp(packet, condition, ilpConditionHandlerImplDpc.getTransactionFromIlpPacket(packet));
    }

    public boolean isValidPacketAgainstCondition(String packet, String condition) {
        return ilpConditionHandlerImplDpc.generateCondition(packet, ilpSecret).equals(condition);
   }

    public void validateFulfillmentAgainstCondition(String fulfillment, String condition) {
        if (!isValidFulfillmentAgainstCondition(fulfillment, condition))
            throw new RuntimeException("ILP Condition is not valid. Fulfillment: " + fulfillment + ", condition: " + condition);
    }

    public boolean isValidFulfillmentAgainstCondition(String fulfillment, String condition) {
        return ilpConditionHandlerImplDpc.validateFulfillmentAgainstCondition(fulfillment, condition);
    }

    public String buildIlpAddress(Transaction transaction) {
        com.ilp.conditions.models.pdp.PartyIdInfo partyIdInfo = transaction.getPayee().getPartyIdInfo();
        return String.format(ILP_ADDRESS_TEMPLATE, partyIdInfo.getFspId(), partyIdInfo.getPartyIdType(), partyIdInfo.getPartyIdentifier());
    }

    private Transaction mapToTransaction(QuoteSwitchRequestDTO request) {
        return mapToTransaction(request.getTransactionId(), request.getQuoteId(), request.getAmount().getIlpMoney(), request.getPayer(), request.getPayee());
    }

    private Transaction mapToTransaction(String transactionId, String quoteId, BigDecimal transactionAmount, String currency,
                                         org.openmf.psp.dto.Party payer, org.openmf.psp.dto.Party payee) {
        Money money = new Money();
        money.setAmount(ContextUtil.formatAmount(transactionAmount));
        money.setCurrency(currency);

        return mapToTransaction(transactionId, quoteId, money, payer, payee);
    }

    private Transaction mapToTransaction(String transactionId, String quoteId, Money transactionAmount, org.openmf.psp.dto.Party payer, org.openmf.psp.dto.Party payee) {
        Transaction transaction = new Transaction();

        transaction.setTransactionId(transactionId);
        transaction.setQuoteId(quoteId);
        transaction.setAmount(transactionAmount);
        transaction.setPayer(payer.getIlpParty());
        transaction.setPayee(payee.getIlpParty());

        return transaction;
    }
}