/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */

package org.openmf.psp.gsma.config;

/**
@Author Sidhant Gupta
*/

import javax.annotation.PostConstruct;

import org.openmf.psp.config.ApplicationSettings;
import org.openmf.psp.config.Binding;
import org.openmf.psp.config.Header;
import org.openmf.psp.config.HubSettings;
import org.openmf.psp.config.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("ott-settings")
public class GSMASettings extends ApplicationSettings<GSMASettings.OttHeader, GSMASettings.OttOperation, GSMASettings.OttBinding> {

    private boolean corsEnabled;
    private String apikey;

    HubSettings hubSettings;

    GSMASettings() {
    }

    @Autowired
    public GSMASettings(HubSettings hubSettings) {
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