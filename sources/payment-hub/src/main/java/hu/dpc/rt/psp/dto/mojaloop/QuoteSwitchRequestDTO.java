/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.dto.mojaloop;

import hu.dpc.rt.psp.dto.Extension;
import hu.dpc.rt.psp.dto.GeoCode;
import hu.dpc.rt.psp.dto.MoneyData;
import hu.dpc.rt.psp.dto.Party;
import hu.dpc.rt.psp.dto.TransactionType;
import hu.dpc.rt.psp.type.AmountType;
import hu.dpc.rt.psp.util.ContextUtil;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.List;

/**
 * {
 * "quoteId": "ae87c51c141611e9ab14d663bd873d93",
 * "transactionId": "1cec02ee141611e9ab14d663bd873d93",
 * "transactionRequestId": "c992aed6141a11e9ab14d663bd873d93",
 * "payee": {
 *      ...Party
 * },
 * "payer": {
 *      ...Party
 * },
 * "amountType": "RECEIVE",
 * "amount": {
 *  "amount": "2000",
 *  "currency": "TZS"
 * },
 * "fees": {
 *  "amount": "2",
 *  "currency": "TZS"
 * },
 * "transactionType": {
 *  "scenario": "PAYMENT",
 *  "subScenario": null,
 *  "initiator": "PAYEE",
 *  "initiatorType": "CONSUMER"
 * },
 * "geoCode": {
 *     "latitude":
 *     "longitude":
 * },
 * "note":,
 * "expiration":,
 * "extensionList": {
 *     "extension": {
 *         "key":,
 *         "value":
 *     }
 * }
 * }
 */
public class QuoteSwitchRequestDTO {

    private String transactionId;
    private String transactionRequestId;
    private String quoteId;
    private Party payee;
    private Party payer;
    private AmountType amountType;
    private MoneyData amount;
    private MoneyData fees;
    private TransactionType transactionType;
    private GeoCode geoCode;
    private String note;
    private String expiration;
    private List<Extension> extensionList;


    QuoteSwitchRequestDTO() {
    }

    public QuoteSwitchRequestDTO(String transactionId, String transactionRequestId, String quoteId, Party payee, Party payer,
                                 AmountType amountType, MoneyData amount, MoneyData fees, TransactionType transactionType,
                                 GeoCode geoCode, String note, LocalDateTime expiration, List<Extension> extensionList) {
        this.transactionId = transactionId;
        this.transactionRequestId = transactionRequestId;
        this.quoteId = quoteId;
        this.payee = payee;
        this.payer = payer;
        this.amountType = amountType;
        this.amount = amount;
        this.fees = fees;
        this.transactionType = transactionType;
        this.geoCode = geoCode;
        this.note = note;
        this.expiration = ContextUtil.formatDate(expiration);
        this.extensionList = extensionList;
    }

    public QuoteSwitchRequestDTO(String transactionId, String quoteId, Party payee, Party payer, AmountType amountType,
                                 MoneyData amount, TransactionType transactionType) {
        this(transactionId, null, quoteId, payee, payer, amountType, amount, null, transactionType, null, null, null, null);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionRequestId() {
        return transactionRequestId;
    }

    public void setTransactionRequestId(String transactionRequestId) {
        this.transactionRequestId = transactionRequestId;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public Party getPayee() {
        return payee;
    }

    public void setPayee(Party payee) {
        this.payee = payee;
    }

    public Party getPayer() {
        return payer;
    }

    public void setPayer(Party payer) {
        this.payer = payer;
    }

    public AmountType getAmountType() {
        return amountType;
    }

    public void setAmountType(AmountType amountType) {
        this.amountType = amountType;
    }

    public MoneyData getAmount() {
        return amount;
    }

    public void setAmount(MoneyData amount) {
        this.amount = amount;
    }

    public MoneyData getFees() {
        return fees;
    }

    public void setFees(MoneyData fees) {
        this.fees = fees;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public GeoCode getGeoCode() {
        return geoCode;
    }

    public void setGeoCode(GeoCode geoCode) {
        this.geoCode = geoCode;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    @Transient
    public LocalDateTime getExpirationDate() {
        return ContextUtil.parseDate(expiration);
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = ContextUtil.formatDate(expiration);
    }

    public List<Extension> getExtensionList() {
        return extensionList;
    }

    public void setExtensionList(List<Extension> extensionList) {
        this.extensionList = extensionList;
    }
}
