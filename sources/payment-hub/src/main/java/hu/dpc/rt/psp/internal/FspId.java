/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.internal;

public class FspId {

    private String instance;
    private String tenant;

    public FspId(String instance, String tenant) {
        this.instance = instance;
        this.tenant = tenant;
    }

    public String getInstance() {
        return instance;
    }

    public String getTenant() {
        return tenant;
    }

    public String getId() {
        return instance + tenant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FspId fspId = (FspId) o;

        if (!instance.equals(fspId.instance)) return false;
        return tenant.equals(fspId.tenant);
    }

    @Override
    public int hashCode() {
        int result = instance.hashCode();
        result = 31 * result + tenant.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FspId{" +
                "instance:'" + instance + '\'' +
                ", tenant:'" + tenant + '\'' +
                '}';
    }
}
