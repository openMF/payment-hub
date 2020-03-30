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
import org.openmf.psp.type.TransactionRequestState;

/**
 * {
 * 	transactionCode: "",
 * 	quoteCode: "",
 * 	fspFee: {
 * 	    amount:
 * 		currency:
 *  },
 * 	fspCommission: {
 * 		amount:
 * 		currency:
 *  },
 * 	expiration: LocalDateTime,
 * 	extensiontList: [
 * 	    {
 * 	        key: "",
 * 	        value:
 * 	    }
 * 	]
 * }
 */
public class QuoteFspResponseDTO {

    private String transactionCode; //mandatory
    private String quoteCode; //mandatory
    private TransactionRequestState state; //mandatory
    private FspMoneyData fspFee;
    private FspMoneyData fspCommission;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime expiration;
    private List<Extension> extensionList;

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getQuoteCode() {
        return quoteCode;
    }

    public void setQuoteCode(String quoteCode) {
        this.quoteCode = quoteCode;
    }

    public TransactionRequestState getState() {
        return state;
    }

    public void setState(TransactionRequestState state) {
        this.state = state;
    }

    public FspMoneyData getFspFee() {
        return fspFee;
    }

    public void setFspFee(FspMoneyData fspFee) {
        this.fspFee = fspFee;
    }

    public FspMoneyData getFspCommission() {
        return fspCommission;
    }

    public void setFspCommission(FspMoneyData fspCommission) {
        this.fspCommission = fspCommission;
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

    public void setExtensionList(List<Extension> extensionList) {
        this.extensionList = extensionList;
    }
}
