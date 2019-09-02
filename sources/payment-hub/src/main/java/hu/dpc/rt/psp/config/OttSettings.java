/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties("ott-settings")
public class OttSettings extends ApplicationSettings<OttSettings.OttHeader, OttSettings.OttOperation, OttSettings.OttBinding> {

    private boolean corsEnabled;
    private String apikey;

    HubSettings hubSettings;

    OttSettings() {
    }

    @Autowired
    public OttSettings(HubSettings hubSettings) {
        this.hubSettings = hubSettings;
    }

    @PostConstruct
    public void postConstruct() {
        postConstruct(hubSettings);
    }

    public boolean isCorsEnabled() {
        return corsEnabled;
    }

    public void setCorsEnabled(boolean corsEnabled) {
        this.corsEnabled = corsEnabled;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public enum OttHeader implements Header {
        TENANT("tenant"),
        ;

        private final String configName;

        OttHeader(String configName) {
            this.configName = configName;
        }
        @Override

        public String getConfigName() {
            return configName;
        }
    }

    public enum  OttOperation implements Operation {
        TRANSACTIONS("transactions"),
        ACCOUNTS("accounts"),
        ;

        private final String configName;

         OttOperation(String configName) {
            this.configName = configName;
        }

        @Override
        public String getConfigName() {
            return configName;
        }
    }

    public enum  OttBinding implements Binding {
        MERCHANTPAYMENT("merchantpayment"),
        TRANSFER("transfer")
        ;

        private final String configName;

        OttBinding(String configName) {
            this.configName = configName;
        }

        @Override
        public String getConfigName() {
            return configName;
        }
    }
}
