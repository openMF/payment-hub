/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.mojaloop.constant;

public enum ExchangeHeader {

    TRANSACTION_ID("Transaction-Id"), // String

    CURRENT_FSP_ID("Current-Fsp-Id"), // FspId
    CURRENT_ROLE("Current-Role"), // TransactionRole
    CALLER_FSP("Caller-Fsp-Id"), // FspId - switch caller FSPIOP-Source

    PAYER_FSP_ID("Payer-FSP-Id"), // FspId
    PAYEE_FSP_ID("Payee-FSP-Id"), // FspId

    PARTIES_ROLE("Parties-Role"), // TransactionRole
    QUOTES_ROLE("Quotes-Role"), // TransactionRole
    TRANSFER_ROLE("Transfer-Role"), // TransactionRole

    PARTIES_INFO("Parties-Info"), // PartyIdInfo
    NOTIFY("Notify"), // NotifyBuilder
    ;

    private String key;

    private ExchangeHeader(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
