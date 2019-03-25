/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.dto.mojaloop;

public class ParticipantSwitchResponseDTO {

    private String fspId;

    public String getFspId() {
        return fspId;
    }

    public void setFspId(String fspId) {
        this.fspId = fspId;
    }
}
