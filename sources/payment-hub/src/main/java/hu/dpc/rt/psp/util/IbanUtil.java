/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.util;

import hu.dpc.rt.psp.config.IbanSettings;
import hu.dpc.rt.psp.internal.FspId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class IbanUtil {

    @Autowired
    private IbanSettings ibanSettings;

    private int fspInstanceIdFirstIndex;
    private int fspInstanceIdLength;
    private int fspInstanceIdLastIndex;
    private int fspTenantIdFirstIndex;
    private int fspTenantIdLength;
    private int fspTenantIdLastIndex;

    @PostConstruct
    public void postConstruct() {
        fspInstanceIdFirstIndex = ibanSettings.getFspInstanceIdFirstIndex();
        fspInstanceIdLength = ibanSettings.getFspInstanceIdLength();
        fspInstanceIdLastIndex = fspInstanceIdFirstIndex + fspInstanceIdLength;
        fspTenantIdFirstIndex = ibanSettings.getFspTenantIdFirstIndex();
        fspTenantIdLength = ibanSettings.getFspTenantIdLength();
        fspTenantIdLastIndex = fspTenantIdFirstIndex + fspTenantIdLength;
    }

    public FspId extractFspIdFromIban(String iban) {
        String fspInstance = iban.substring(fspInstanceIdFirstIndex, fspInstanceIdLastIndex);
        String fspTenant = iban.substring(fspTenantIdFirstIndex, fspTenantIdLastIndex);

        return new FspId(fspInstance, fspTenant);
    }

    public String extractAccountIdFromIban(String iban) {

        return iban.substring(fspTenantIdLastIndex);
    }
}
