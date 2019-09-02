/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtil {

    private static ObjectMapper mapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);


    public static String toJson(Object pojo) {

        try {
            return mapper.writeValueAsString(pojo);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("Error while mapping from POJO (%s) to JSON string!", pojo.getClass()), e);
        }

    }

    public static <T extends Object> T toPojo(String json, Class<T> type) {

        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error while mapping from JSON string to POJO (%s)!", type), e);
        }

    }
}
