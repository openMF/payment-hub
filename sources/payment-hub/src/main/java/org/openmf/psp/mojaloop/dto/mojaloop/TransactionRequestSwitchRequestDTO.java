/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.mojaloop.dto.mojaloop;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.List;

import org.openmf.psp.dto.Extension;
import org.openmf.psp.dto.GeoCode;
import org.openmf.psp.dto.MoneyData;
import org.openmf.psp.dto.Party;
import org.openmf.psp.dto.PartyIdInfo;
import org.openmf.psp.dto.TransactionType;
import org.openmf.psp.type.AuthenticationType;

/**
 * {
 * 	accountId: ""
 * }
 */
public class TransactionRequestSwitchRequestDTO {

    private String transactionRequestId; // mandatory
    private Party payee; // mandatory
    private PartyIdInfo payer; // mandatory
    private MoneyData amount; // mandatory
    private TransactionType transactionType; // mandatory
    private String note;
    private GeoCode geoCode;
    private AuthenticationType authenticationType;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime expiration;
    private List<Extension> extensionList;


    public String getTransactionRequestId() {
        return transactionRequestId;
    }

    public void setTransactionRequestId(String transactionRequestId) {
        this.transactionRequestId = transactionRequestId;
    }

    public Party getPayee() {
        return payee;
    }

    public void setPayee(Party payee) {
        this.payee = payee;
    }

    public PartyIdInfo getPayer() {
        return payer;
    }

    public void setPayer(PartyIdInfo payer) {
        this.payer = payer;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public GeoCode getGeoCode() {
        return geoCode;
    }

    public void setGeoCode(GeoCode geoCode) {
        this.geoCode = geoCode;
    }

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    public List<Extension> getExtensionList() {
        return extensionList;
    }

    public Extension getExtension(String key) {
        if (extensionList == null)
            return null;
        for (Extension extension : extensionList) {
            if (extension.getKey().equals(key))
                return extension;
        }
        return null;
    }

    public String getExtensionValue(String key) {
        Extension extension = getExtension(key);
        return extension == null ? null : extension.getValue();
    }

    public void setExtensionList(List<Extension> extensionList) {
        this.extensionList = extensionList;
    }
}
