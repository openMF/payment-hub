/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.dto.fsp;

import org.openmf.psp.dto.FspMoneyData;
import org.openmf.psp.dto.TransactionType;
import org.openmf.psp.type.TransactionRole;

/**
 * {
 *   "transactionCode": "ae87c51c141611e9ab14d663bd873d93",
 *   "transferCode": "31d77b06141c11e9ab14d663bd873d93",
 *   "accountId": "c14ee40813a511e9ab14d663bd873d93",
 *   "amount": {
 *     "amount": "2000",
 *     "currency": "IDR"
 *   },
 *   "fspFee": {
 *     "amount": "100",
 *     "currency": "IDR"
 *   },
 *   "fspCommission": null,
 *   "transactionRole": "PAYER",
 *   "transactionType": {
 *   	"scenario": "PAYMENT",
 *   	"subScenario": null,
 *   	"initiator": "PAYEE",
 *   	"initiatorType": "CONSUMER"
 *   }
 * }
 */
public class TransferFspRequestDTO {

    private String transactionCode;
    private String transferCode;
    private String accountId;
    private FspMoneyData amount;
    private FspMoneyData fspFee;
    private FspMoneyData fspCommission;
    private TransactionRole transactionRole;
    private TransactionType transactionType;
    private String note;

    TransferFspRequestDTO() {
    }

    public TransferFspRequestDTO(String transactionCode, String transferCode, String accountId, FspMoneyData amount, FspMoneyData fspFee,
                                 FspMoneyData fspCommission, TransactionRole transactionRole, TransactionType transactionType, String note) {
        this.transactionCode = transactionCode;
        this.transferCode = transferCode;
        this.accountId = accountId;
        this.amount = amount;
        this.fspFee = fspFee;
        this.fspCommission = fspCommission;
        this.transactionRole = transactionRole;
        this.transactionType = transactionType;
        this.note = note;
    }

    public TransferFspRequestDTO(String transactionCode, String transferCode, String accountId, FspMoneyData amount, TransactionRole transactionRole) {
        this(transactionCode, transferCode, accountId, amount, null, null, transactionRole, null, null);
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getTransferCode() {
        return transferCode;
    }

    public void setTransferCode(String transferCode) {
        this.transferCode = transferCode;
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
}
