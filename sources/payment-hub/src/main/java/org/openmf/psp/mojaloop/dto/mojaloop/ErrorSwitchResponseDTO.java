/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.mojaloop.dto.mojaloop;


import org.openmf.psp.dto.ErrorInformation;

public class ErrorSwitchResponseDTO {

    private ErrorInformation errorInformation;

    public ErrorInformation getErrorInformation() {
        return errorInformation;
    }

    public void setErrorInformation(ErrorInformation errorInformation) {
        this.errorInformation = errorInformation;
    }

    @Override
    public String toString() {
        return "ErrorSwitchResponseDTO{" +
                "errorInformation:" + errorInformation +
                '}';
    }
}
