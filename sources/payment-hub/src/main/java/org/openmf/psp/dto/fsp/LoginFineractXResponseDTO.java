/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.dto.fsp;

import java.util.Collection;
import java.util.Date;

public class LoginFineractXResponseDTO implements LoginFspResponse {

    private String username;
    private Long userId;
    private String base64EncodedAuthenticationKey;
    
    private boolean authenticated;
    private boolean shouldRenewPassword;
    private boolean isTwoFactorAuthenticationRequired;

    private Long officeId;
    
    private String officeName;

    
    private Collection<RoleData> roles;
    
    private Collection<String> permissions;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBase64EncodedAuthenticationKey() {
        return base64EncodedAuthenticationKey;
    }

    public void setBase64EncodedAuthenticationKey(String base64EncodedAuthenticationKey) {
        this.base64EncodedAuthenticationKey = base64EncodedAuthenticationKey;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public boolean isShouldRenewPassword() {
        return shouldRenewPassword;
    }

    public void setShouldRenewPassword(boolean shouldRenewPassword) {
        this.shouldRenewPassword = shouldRenewPassword;
    }

    public boolean getIsTwoFactorAuthenticationRequired() {
        return isTwoFactorAuthenticationRequired;
    }

    public void setIsTwoFactorAuthenticationRequired(boolean isTwoFactorAuthenticationRequired) {
        this.isTwoFactorAuthenticationRequired = isTwoFactorAuthenticationRequired;
    }

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public Collection<RoleData> getRoles() {
        return roles;
    }

    public void setRoles(Collection<RoleData> roles) {
        this.roles = roles;
    }

    public Collection<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String getAccessToken() {
        return base64EncodedAuthenticationKey;
    }

    @Override
    public Date getAccessTokenExpiration() {
        return shouldRenewPassword ? new Date(System.currentTimeMillis() + 5 * 60 * 60 * 1000) : null; //TODO: TIMEZONE
    }
}
