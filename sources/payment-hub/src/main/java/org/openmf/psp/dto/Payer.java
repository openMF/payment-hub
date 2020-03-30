/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.dto;

public class Payer {

    private PartyIdInfo partyIdInfo;

    public PartyIdInfo getPartyIdInfo() {
        return partyIdInfo;
    }

    public void setPartyIdInfo(PartyIdInfo partyIdInfo) {
        this.partyIdInfo = partyIdInfo;
    }
}
