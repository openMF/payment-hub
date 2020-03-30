/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.dto;

import java.util.Arrays;
import java.util.List;

public class ErrorInformation {

    private short errorCode; //mandatory, 4 digits
    private String errorDescription; //mandatory
    private List<Extension> extensionList;

    public short getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(short errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public List<Extension> getExtensionList() {
        return extensionList;
    }

    public void setExtensionList(List<Extension> extensionList) {
        this.extensionList = extensionList;
    }

    @Override
    public String toString() {
        return "ErrorInformation{" +
                "errorCode:" + errorCode +
                ", errorDescription:'" + errorDescription + '\'' +
                ", extensionList:" + (extensionList == null ? "" : Arrays.toString(extensionList.toArray())) +
                '}';
    }
}
