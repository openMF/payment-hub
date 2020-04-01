/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */

/**
@Author Sidhant Gupta
*/
package org.openmf.psp.gsma.dto;

/*
{
    "status": "available",
    "subStatus": null,
    "lei": "AAAA0012345678901299"
}
 */
//Refactor to use interoperability APIs instead.
public class AccountStatus {

    String status;
    String subStatus;
    String lei;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(String subStatus) {
        this.subStatus = subStatus;
    }

    public String getLei() {
        return lei;
    }

    public void setLei(String lei) {
        this.lei = lei;
    }
}