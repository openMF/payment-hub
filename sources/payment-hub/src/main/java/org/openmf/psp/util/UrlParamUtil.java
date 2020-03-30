/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.util;

public class UrlParamUtil {

    //TODO: rewrite this for better performance
    public static String extractFromContextPath(String contextPath, int index) {

        //ContextPath starts with a leading slash
        return contextPath.split("/")[index];
    }

}
