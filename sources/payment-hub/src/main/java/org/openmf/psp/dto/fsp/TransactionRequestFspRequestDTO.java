/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.dto.fsp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.List;

import org.openmf.psp.dto.Extension;
import org.openmf.psp.dto.FspMoneyData;
import org.openmf.psp.dto.GeoCode;
import org.openmf.psp.dto.TransactionType;
import org.openmf.psp.type.TransactionRole;


public class TransactionRequestFspRequestDTO {

    private String transactionCode; // mandatory
    private String requestCode; // mandatory
    private String accountId; // mandatory
    private FspMoneyData amount; // mandatory
    private TransactionRole transactionRole; // mandatory
    private TransactionType transactionType;
    private String note;
    private GeoCode geoCode;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime expiration;
    private List<Extension> extensionList;

    TransactionRequestFspRequestDTO() {
    }

    public TransactionRequestFspRequestDTO(String transactionCode, String requestCode, String accountId, FspMoneyData amount,
                                           TransactionRole transactionRole, TransactionType transactionType, String note,
                                           GeoCode geoCode, LocalDateTime expiration, List<Extension> extensionList) {
        this.transactionCode = transactionCode;
        this.requestCode = requestCode;
        this.accountId = accountId;
        this.amount = amount;
        this.transactionRole = transactionRole;
        this.transactionType = transactionType;
        this.note = note;
        this.geoCode = geoCode;
        this.expiration = expiration;
        this.extensionList = extensionList;
    }

    public TransactionRequestFspRequestDTO(String transactionCode, String requestCode, String accountId, FspMoneyData amount,
                                           TransactionRole transactionRole) {
        this(transactionCode, requestCode, accountId, amount, transactionRole, null, null, null, null, null);
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public FspMoneyData getAmount() {
        return amount;
    }

    public void setAmount(FspMoneyData amount) {
        this.amount = amount;
    }

    public TransactionRole getTransactionRole() {
        return transactionRole;
    }

    public void setTransactionRole(TransactionRole transactionRole) {
        this.transactionRole = transactionRole;
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
        for (Extension extension : extensionList) {
            if (extension.getKey().equals(key))
                return extension;
        }
        return null;
    }

    public void setExtensionList(List<Extension> extensionList) {
        this.extensionList = extensionList;
    }
}
