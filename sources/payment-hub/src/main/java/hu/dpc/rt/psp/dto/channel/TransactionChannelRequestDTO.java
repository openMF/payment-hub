/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.dto.channel;

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
 *   "clientRefId": "{{ch_client_ref}}",
 *   "payer": {
 *     "partyIdInfo": {
 *       "partyIdType": "IBAN",
 *       "partyIdentifier": "{{IBAN_prefix}}{{fsp_payer_id}}{{fsp_payer_tenant1}}{{fsp_payer_account1}}"
 *     }
 *   },
 *   "payee": {
 *     "partyIdInfo": {
 *       "partyIdType": "IBAN",
 *       "partyIdentifier": "{{IBAN_prefix}}{{fsp_payee_id}}{{fsp_payee_tenant1}}{{fsp_payee_account1}}"
 *     },
 *     "merchantClassificationCode": ""
 *   },
 *   "amountType": "RECEIVE",
 *   "amount": {
 *     "amount": {{amount}},
 *     "currency": "{{currency}}"
 *   },
 *   "transactionType": {
 *     "scenario": "PAYMENT",
 *     "initiator": "PAYER",
 *     "initiatorType": "CONSUMER"
 *   },
 *   "note": "Demo interoperation merchant payment",
 *   "expiration": "2019-12-31T00:00:00.000-01:00"
 * }
 */
public class TransactionChannelRequestDTO {

    private String clientRefId;
    private Party payer;
    private Party payee;
    private AmountType amountType;
    private MoneyData amount;
    private TransactionType transactionType;
    private GeoCode geoCode;
    private String note;
    private String expiration;
    private List<Extension> extensionList;

    public String getClientRefId() {
        return clientRefId;
    }

    public void setClientRefId(String clientRefId) {
        this.clientRefId = clientRefId;
    }

    public Party getPayer() {
        return payer;
    }

    public void setPayer(Party payer) {
        this.payer = payer;
    }

    public Party getPayee() {
        return payee;
    }

    public void setPayee(Party payee) {
        this.payee = payee;
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

    @Override
    public String toString() {
        return "TransactionChannelRequestDTO{" +
                "clientRefId='" + clientRefId + '\'' +
                ", payer=" + payer +
                ", payee=" + payee +
                ", amountType=" + amountType +
                ", amount=" + amount +
                ", transactionType=" + transactionType +
                ", note='" + note + '\'' +
                ", expiration=" + expiration +
                '}';
    }
}


