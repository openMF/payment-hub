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
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties("channel-settings")
public class ChannelSettings extends ApplicationSettings<ChannelSettings.ChannelHeader, ChannelSettings.ChannelOperation, ChannelSettings.ChannelBinding> {

    private boolean corsEnabled;

    HubSettings hubSettings;

    ChannelSettings() {
    }

    @Autowired
    public ChannelSettings(HubSettings hubSettings) {
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

    public enum ChannelHeader implements Header {
        TENANT("tenant"),
        ;

        private final String configName;

        ChannelHeader(String configName) {
            this.configName = configName;
        }
        @Override

        public String getConfigName() {
            return configName;
        }
    }

    public enum ChannelOperation implements Operation {
        NOTIFICATION_QUOTES("quotes"),
        NOTIFICATION_TRANSFERS("transfers"),
        RESPONSE("response"),
        ;

        private final String configName;

        ChannelOperation(String configName) {
            this.configName = configName;
        }

        @Override
        public String getConfigName() {
            return configName;
        }
    }

    public enum ChannelBinding implements Binding {
        PAYMENT("payment"),
        STATUS("status"),
        CLIENT_STATUS("client-status"),
        PARTIES("parties"),
        ;

        private final String configName;

        ChannelBinding(String configName) {
            this.configName = configName;
        }

        @Override
        public String getConfigName() {
            return configName;
        }
    }
}
