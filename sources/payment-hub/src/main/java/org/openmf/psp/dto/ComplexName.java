/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.dto;

public class ComplexName extends PartyIdInfo {

    private String firstName;
    private String middleName;
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    void update(ComplexName oName) {
        if (oName == null)
            return;

        String oFn = oName.firstName;
        if (oFn != null)
            firstName = oFn;

        String oMn = oName.middleName;
        if (oFn != null)
            middleName = oMn;

        String oLn = oName.lastName;
        if (oLn != null)
            lastName = oLn;
    }
}
