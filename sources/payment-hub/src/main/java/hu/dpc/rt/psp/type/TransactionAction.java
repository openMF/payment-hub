/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.type;

import java.util.Arrays;

public enum TransactionAction {

    REQUEST,
    REQUEST_FAILED,
    QUOTE,
    QUOTE_FAILED,
    PREPARE,
    PREPARE_FAILED,
    COMMIT,
    COMMIT_FAILED,
    ABORT;


    public static TransactionAction[] VALUES = values();
    
    private static TransactionAction[][] validTransitions = new TransactionAction[TransactionAction.VALUES.length + 1][TransactionAction.VALUES.length];

    static {
        validTransitions[TransactionAction.VALUES.length][TransactionAction.REQUEST.ordinal()] = TransactionAction.REQUEST;
        validTransitions[TransactionAction.VALUES.length][TransactionAction.REQUEST_FAILED.ordinal()] = TransactionAction.REQUEST_FAILED;

        validTransitions[TransactionAction.VALUES.length][TransactionAction.QUOTE.ordinal()] = TransactionAction.QUOTE;
        validTransitions[TransactionAction.REQUEST.ordinal()][TransactionAction.QUOTE.ordinal()] = TransactionAction.QUOTE;
        validTransitions[TransactionAction.VALUES.length][TransactionAction.QUOTE_FAILED.ordinal()] = TransactionAction.QUOTE_FAILED;
        validTransitions[TransactionAction.REQUEST.ordinal()][TransactionAction.QUOTE_FAILED.ordinal()] = TransactionAction.QUOTE_FAILED;

        validTransitions[TransactionAction.QUOTE.ordinal()][TransactionAction.PREPARE.ordinal()] = TransactionAction.PREPARE;
        validTransitions[TransactionAction.QUOTE.ordinal()][TransactionAction.PREPARE_FAILED.ordinal()] = TransactionAction.PREPARE_FAILED;

        validTransitions[TransactionAction.QUOTE.ordinal()][TransactionAction.COMMIT.ordinal()] = TransactionAction.COMMIT;
        validTransitions[TransactionAction.PREPARE.ordinal()][TransactionAction.COMMIT.ordinal()] = TransactionAction.COMMIT;
        validTransitions[TransactionAction.QUOTE.ordinal()][TransactionAction.COMMIT_FAILED.ordinal()] = TransactionAction.COMMIT_FAILED;
        validTransitions[TransactionAction.PREPARE.ordinal()][TransactionAction.COMMIT_FAILED.ordinal()] = TransactionAction.COMMIT_FAILED;

        validTransitions[TransactionAction.VALUES.length][TransactionAction.ABORT.ordinal()] = TransactionAction.ABORT;
        validTransitions[TransactionAction.REQUEST.ordinal()][TransactionAction.ABORT.ordinal()] = TransactionAction.ABORT;
        validTransitions[TransactionAction.QUOTE.ordinal()][TransactionAction.ABORT.ordinal()] = TransactionAction.ABORT;
        validTransitions[TransactionAction.PREPARE.ordinal()][TransactionAction.ABORT.ordinal()] = TransactionAction.ABORT;
    }

    /**
     * Executes the given transition from one status to another when the given transition (actualEvent + action) is valid.
     *
     * @param currentState actual {@link TransactionAction}
     * @param action        the {@link TransactionAction}
     * @return the new {@link TransactionAction} for the tag if the given transition is valid
     *
     */
    public static TransactionAction handleTransition(TransactionAction currentState, TransactionAction action) {
        TransactionAction destinationStatus = getTransitionState(currentState, action);
        if (destinationStatus == null) {
            throw new UnsupportedOperationException("State transition is not valid: " + currentState + " and action: " + action);
        }
        return destinationStatus;
    }

    public static boolean isValidAction(TransactionAction currentState, TransactionAction action) {
        return getTransitionState(currentState, action) != null;
    }

    public static TransactionAction[] getValidActions(TransactionAction currentState) {
        return Arrays.stream(TransactionAction.VALUES).filter(a -> isValidAction(currentState, a)).toArray(TransactionAction[]::new);
    }

    private static TransactionAction getTransitionState(TransactionAction currentState, TransactionAction action) {
        return validTransitions[currentState == null ? TransactionAction.VALUES.length : currentState.ordinal()][action.ordinal()];
    }

    public boolean isFailed() {
        return this == REQUEST_FAILED || this == QUOTE_FAILED || this == PREPARE_FAILED || this == COMMIT_FAILED;
    }
}
