/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.type;

public enum TransferState {

    RECEIVED,
    RESERVED,
    COMMITTED,
    ABORTED;

    public static TransferState forAction(TransactionAction action) {
        switch (action) {
            case REQUEST:
            case QUOTE:
                return RECEIVED;
            case PREPARE:
                return RESERVED;
            case COMMIT:
                return COMMITTED;
            case REQUEST_FAILED:
            case QUOTE_FAILED:
            case PREPARE_FAILED:
            case COMMIT_FAILED:
            case ABORT:
                return ABORTED;
            default:
                return null; // TODO fail
        }
    }
}
