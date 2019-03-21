/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.dto;

public class PersonalInfo {

    private ComplexName complexName;
    private String dateOfBirth;

    public ComplexName getComplexName() {
        return complexName;
    }

    public void setComplexName(ComplexName complexName) {
        this.complexName = complexName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    void update(PersonalInfo oInfo) {
        if (oInfo == null)
            return;

        ComplexName oName = oInfo.complexName;
        if (complexName == null)
            complexName = oName;
        else
            complexName.update(oName);

        String oDate = oInfo.dateOfBirth;
        if (oDate != null)
            dateOfBirth = oDate;
    }
}
