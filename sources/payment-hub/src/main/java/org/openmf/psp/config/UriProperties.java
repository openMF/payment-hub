/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.config;

import org.apache.logging.log4j.util.Strings;

public abstract class UriProperties {

    private String name;
    private String user;
    private String password;
    private String host;
    private String port;
    private String base;

    protected UriProperties() {
    }

    protected UriProperties(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getUrl() {
        return host + (Strings.isEmpty(port) ? "" : (':' + port)) + (Strings.isEmpty(base) ? "" : ((base.charAt(0) == '/' ? "" : '/') + base));
    }

    protected abstract String getDefaultName();

    boolean isDefault() {
        return name.equals(getDefaultName());
    }

    void postConstruct(UriProperties oProps) {
        if (oProps == null || oProps == this)
            return;

        // empty is a valid value
        if (user == null)
            user = oProps.getUser();
        if (password == null)
            password = oProps.getPassword();
        if (host == null)
            host = oProps.getHost();
        if (port == null)
            port = oProps.getPort();
        if (base == null)
            base = oProps.getBase();
    }
}
