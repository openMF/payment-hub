package org.openmf.psp.mpesa.config;

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
@ConfigurationProperties("mpesa-settings")
public class MPesaSettings extends ApplicationSettings<MPesaSettings.MPesaHeader, MPesaSettings.MPesaOperation, MPesaSettings.MPesaBinding> {

    private String consumerKey;
    private String consumerSecret;

    HubSettings hubSettings;

    MPesaSettings() {

    }

    @Autowired
    public MPesaSettings(HubSettings hubSettings) {
        this.hubSettings = hubSettings;
    }

    @PostConstruct
    public void postConstruct() {
        postConstruct(hubSettings);
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public enum MPesaHeader implements Header {
        TENANT("tenant"); // Arbitrary variable, need to confirm

        private final String configName;

        MPesaHeader(String configName) {
            this.configName = configName;
        }

        @Override
        public String getConfigName() {
            return configName;
        }
    }

    public enum MPesaOperation implements Operation {
        OAUTH("oauth-token"),
        BALANCE("account-balance");

        private final String configName;

        MPesaOperation(String configName) {
            this.configName = configName;
        }

        @Override
        public String getConfigName() {
            return configName;
        }
    }

    public enum MPesaBinding implements Binding {
        BALANCE("balance"); // Arbitrary variable, need to confirm

        private final String configName;

        MPesaBinding(String configName) {
            this.configName = configName;
        }

        @Override
        public String getConfigName() {
            return configName;
        }
    }

}
