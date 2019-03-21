/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.config;

public class TenantProperties extends UriProperties {

    protected TenantProperties() {
    }

    protected TenantProperties(String name) {
        super(name);
    }

    @Override
    protected String getDefaultName() {
        return null; // no default tenant
    }
}
