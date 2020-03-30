/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.config;

import java.util.ArrayList;
import java.util.List;

public abstract class TenantDependentProperties extends UriProperties {

    private List<TenantProperties> tenants;

    protected TenantDependentProperties() {
    }

    protected TenantDependentProperties(String name) {
        super(name);
    }

    public List<TenantProperties> getTenants() {
        return tenants;
    }

    public void setTenants(List<TenantProperties> tenants) {
        this.tenants = tenants == null ? new ArrayList<>(0) : tenants;
    }

    public TenantProperties getTenant(String tenant) {
        if (tenant == null || tenants == null)
            return null;
        for (TenantProperties channelTenant : tenants) {
            if (tenant.equals(channelTenant.getName()))
                return channelTenant;
        }
        return null;
    }

    protected TenantProperties addTenant(TenantProperties tenant) {
        if (tenants == null)
            tenants = new ArrayList<>(1);
        tenants.add(tenant);
        return tenant;
    }

    void postConstruct(TenantDependentProperties oProps) {
        super.postConstruct(oProps);

        if (tenants == null)
            return;

        for (TenantProperties tenant : tenants) {
            tenant.postConstruct(this);
            if (oProps != null && oProps != this) {
                tenant.postConstruct(oProps.getTenant(tenant.getName()));
                tenant.postConstruct(oProps);
            }
        }
    }
}
