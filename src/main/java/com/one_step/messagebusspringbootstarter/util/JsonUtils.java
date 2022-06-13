//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.one_step.messagebusspringbootstarter.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.one_step.messagebusspringbootstarter.error.OsError;


import java.io.IOException;
import java.util.List;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonUtils() {
    }

    public static String writeJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException var2) {
            return null;
        }
    }

    public static <T> T readJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException var4) {
            String error = String.format("Failed to read object from json: %s", var4.getMessage());
            LoggerUtils.error(OsError.OS_FAILURE_JSON_CONVERT, error);
            return null;
        }
    }

    public static <T> List<T> readJsonAsList(String json, Class<T> clazz) {
        try {
            JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return (List)objectMapper.readValue(json, listType);
        } catch (IOException var4) {
            String error = String.format("Failed to read object from json: %s", var4.getMessage());
            LoggerUtils.error(OsError.OS_FAILURE_JSON_CONVERT, error);
            return null;
        }
    }

    static {
        objectMapper.setSerializationInclusion(Include.NON_NULL);
    }
}
