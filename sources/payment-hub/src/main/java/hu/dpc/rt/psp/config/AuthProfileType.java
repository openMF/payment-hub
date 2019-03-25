/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.config;

import org.apache.logging.log4j.util.Strings;

public enum AuthProfileType {
    NONE,
    BASIC {
        protected String getPrefix() {
            return "Basic ";
        }
    },
    BASIC_TWOFACTOR {
        protected String getPrefix() {
            return "Basic ";
        }
    },
    OAUTH {
        protected String getPrefix() {
            return "Bearer ";
        }
    },
    OAUTH_TWOFACTOR {
        protected String getPrefix() {
            return "Bearer ";
        }
    },
    ;


    public static AuthProfileType forConfig(String config) {
        if (Strings.isEmpty(config))
            return NONE;
        return AuthProfileType.valueOf(config.toUpperCase());
    }

    public String encode(String value) {
        if (value == null)
            return null;
        String prefix = getPrefix();
        return Strings.isEmpty(prefix) || value.startsWith(prefix) ? value : (prefix + value);
    }

    protected String getPrefix() {
        return null;
    }
}
