/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.mojaloop.type;

public enum TransactionState {

    RECEIVED,
    PENDING,
    COMPLETED,
    REJECTED;

    public static TransactionState forAction(TransactionAction action) {
        switch (action) {
            case REQUEST:
            case QUOTE:
            case PREPARE:
                return PENDING;
            case COMMIT:
                return COMPLETED;
            case REQUEST_FAILED:
            case QUOTE_FAILED:
            case PREPARE_FAILED:
            case COMMIT_FAILED:
            case ABORT:
                return REJECTED;
            default:
                return null; // TODO fail
        }
    }
}
