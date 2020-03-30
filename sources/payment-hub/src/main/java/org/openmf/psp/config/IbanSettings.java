/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("iban-settings")
public class IbanSettings {

    private int fspInstanceIdFirstIndex;
    private int fspInstanceIdLength;
    private int fspTenantIdFirstIndex;
    private int fspTenantIdLength;

    public int getFspInstanceIdFirstIndex() {
        return fspInstanceIdFirstIndex;
    }

    public void setFspInstanceIdFirstIndex(int fspInstanceIdFirstIndex) {
        this.fspInstanceIdFirstIndex = fspInstanceIdFirstIndex;
    }

    public int getFspInstanceIdLength() {
        return fspInstanceIdLength;
    }

    public void setFspInstanceIdLength(int fspInstanceIdLength) {
        this.fspInstanceIdLength = fspInstanceIdLength;
    }

    public int getFspTenantIdFirstIndex() {
        return fspTenantIdFirstIndex;
    }

    public void setFspTenantIdFirstIndex(int fspTenantIdFirstIndex) {
        this.fspTenantIdFirstIndex = fspTenantIdFirstIndex;
    }

    public int getFspTenantIdLength() {
        return fspTenantIdLength;
    }

    public void setFspTenantIdLength(int fspTenantIdLength) {
        this.fspTenantIdLength = fspTenantIdLength;
    }
}
