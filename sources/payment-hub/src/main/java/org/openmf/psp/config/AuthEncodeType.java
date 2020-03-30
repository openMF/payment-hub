/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */

package org.openmf.psp.config;

import org.apache.logging.log4j.util.Strings;

import java.util.Base64;

public enum AuthEncodeType {
    NONE,
    BASE64 {
        @Override
        public String encode(String value) {
            return value == null ? null : new String(Base64.getEncoder().encode(value.getBytes()));
        }
    },
    ;

    public static AuthEncodeType forConfig(String config) {
        if (Strings.isEmpty(config))
            return NONE;
        return AuthEncodeType.valueOf(config.toUpperCase());
    }

    public String encode(String value) {
        return value;
    }
}
