/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.dto;

import org.openmf.psp.type.InitiatorType;
import org.openmf.psp.type.Scenario;
import org.openmf.psp.type.SubScenario;
import org.openmf.psp.type.TransactionRole;

public class TransactionType {

    private Scenario scenario;
    private SubScenario subScenario;
    private TransactionRole initiator;
    private InitiatorType initiatorType;
    private Refund refundInfo;
    private String balanceOfPayments; // 3 digits number, see https://www.imf.org/external/np/sta/bopcode/

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public SubScenario getSubScenario() {
        return subScenario;
    }

    public void setSubScenario(SubScenario subScenario) {
        this.subScenario = subScenario;
    }

    public TransactionRole getInitiator() {
        return initiator;
    }

    public void setInitiator(TransactionRole initiator) {
        this.initiator = initiator;
    }

    public InitiatorType getInitiatorType() {
        return initiatorType;
    }

    public void setInitiatorType(InitiatorType initiatorType) {
        this.initiatorType = initiatorType;
    }

    public Refund getRefundInfo() {
        return refundInfo;
    }

    public void setRefundInfo(Refund refundInfo) {
        this.refundInfo = refundInfo;
    }

    public String getBalanceOfPayments() {
        return balanceOfPayments;
    }

    public void setBalanceOfPayments(String balanceOfPayments) {
        this.balanceOfPayments = balanceOfPayments;
    }
}
