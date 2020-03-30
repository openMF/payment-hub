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

@ConfigurationProperties("switch-settings")
public class SwitchSettings extends ApplicationSettings<SwitchSettings.SwitchHeader, SwitchSettings.SwitchOperation, SwitchSettings.SwitchBinding> {

    private boolean integrationEnabled;

    HubSettings hubSettings;

    SwitchSettings() {
    }

    @Autowired
    public SwitchSettings(HubSettings hubSettings) {
        this.hubSettings = hubSettings;
    }

    @PostConstruct
    public void postConstruct() {
        postConstruct(hubSettings);
    }

    public boolean isIntegrationEnabled() {
        return integrationEnabled;
    }

    public void setIntegrationEnabled(boolean integrationEnabled) {
        this.integrationEnabled = integrationEnabled;
    }

    public enum SwitchHeader implements Header {
        SOURCE("source"),
        DESTINATION("destination"),
        ;

        private final String configName;

        SwitchHeader(String configName) {
            this.configName = configName;
        }

        @Override
        public String getConfigName() {
            return configName;
        }
    }

    public enum SwitchOperation implements Operation {
        PARTICIPANTS("participants"),
        PARTIES("parties"),
        REQUESTS("requests"),
        QUOTES("quotes"),
        TRANSFERS("transfers"),
        ;

        private final String configName;

        SwitchOperation(String configName) {
            this.configName = configName;
        }

        @Override
        public String getConfigName() {
            return configName;
        }
    }

    public enum SwitchBinding implements Binding {
        PARTICIPANTS("participants"),
        PARTIES("parties"),
        REQUESTS("requests"),
        QUOTES("quotes"),
        TRANSFERS("transfers"),
        ;

        private final String configName;

        SwitchBinding(String configName) {
            this.configName = configName;
        }

        @Override
        public String getConfigName() {
            return configName;
        }
    }
}
