/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.internal;

import hu.dpc.rt.psp.component.FspRestClient;

public class FspContext {

    private FspId fspId;
    private FspRestClient.TenantAuth tenantAuth;

    public FspId getFspId() {
        return fspId;
    }

    public void setFspId(FspId fspId) {
        this.fspId = fspId;
    }

    public FspRestClient.TenantAuth getTenantAuth() {
        return tenantAuth;
    }
}
