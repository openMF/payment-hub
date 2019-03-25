/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.config;

import hu.dpc.rt.psp.dto.fsp.LoginFspResponse;

public class AuthProperties {

    private AuthProfileType profile;
    private AuthEncodeType encode;
    private Class<LoginFspResponse> loginClass;

    protected AuthProperties() {
    }

    public AuthProfileType getProfile() {
        return profile;
    }

    public void setProfile(AuthProfileType profile) {
        this.profile = profile;
    }

    public AuthEncodeType getEncode() {
        return encode;
    }

    public void setEncode(AuthEncodeType encode) {
        this.encode = encode;
    }

    public Class<LoginFspResponse> getLoginClass() {
        return loginClass;
    }

    public void setLoginClass(Class<LoginFspResponse> loginClass) {
        this.loginClass = loginClass;
    }
}
