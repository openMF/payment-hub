/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

@ConfigurationProperties("fsp-settings")
public class FspSettings extends ApplicationSettings<FspSettings.FspHeader, FspSettings.FspOperation, FspSettings.FspBinding> {

    private String ilpSecret;
    private AuthProperties auth;

    HubSettings hubSettings;

    FspSettings() {
    }

    @Autowired
    public FspSettings(HubSettings hubSettings) {
        this.hubSettings = hubSettings;
    }

    @PostConstruct
    public void postConstruct() {
        postConstruct(hubSettings);
    }

    public String getIlpSecret() {
        return ilpSecret;
    }

    public void setIlpSecret(String ilpSecret) {
        this.ilpSecret = ilpSecret;
    }

    public AuthProperties getAuth() {
        return auth;
    }

    public void setAuth(AuthProperties auth) {
        this.auth = auth;
    }

    public enum FspHeader implements Header {
        USER("user"),
        TENANT("tenant"),
        ;

        private final String configName;

        FspHeader(String configName) {
            this.configName = configName;
        }

        @Override
        public String getConfigName() {
            return configName;
        }
    }

    public enum FspOperation implements Operation {
        AUTH("auth"),
        PARTIES("parties"),
        REQUESTS("requests"),
        QUOTES("quotes"),
        TRANSFERS("transfers"),
        ;

        private final String configName;

        FspOperation(String configName) {
            this.configName = configName;
        }

        @Override
        public String getConfigName() {
            return configName;
        }
    }

    public enum FspBinding implements Binding {
        ;
        @Override
        public String getConfigName() {
            return null;
        }
    }
}
