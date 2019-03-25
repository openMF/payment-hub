/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("mock-settings")
public class MockSettings {

    private boolean startChannelConsumers;
    private boolean startMojaloopConsumers;
    private boolean startFspConsumers;

    public boolean isStartChannelConsumers() {
        return startChannelConsumers;
    }

    public void setStartChannelConsumers(boolean startChannelConsumers) {
        this.startChannelConsumers = startChannelConsumers;
    }

    public boolean isStartMojaloopConsumers() {
        return startMojaloopConsumers;
    }

    public void setStartMojaloopConsumers(boolean startMojaloopConsumers) {
        this.startMojaloopConsumers = startMojaloopConsumers;
    }

    public boolean isStartFspConsumers() {
        return startFspConsumers;
    }

    public void setStartFspConsumers(boolean startFspConsumers) {
        this.startFspConsumers = startFspConsumers;
    }
}
