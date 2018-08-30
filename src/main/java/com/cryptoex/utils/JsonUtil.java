package com.cryptoex.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

public class JsonUtil {
    public static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    public JsonUtil() {
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.ALWAYS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    public static String writeJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static <T> T readJson(String str, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(str, clazz);
        } catch (IOException var3) {
            throw new RuntimeException(var3);
        }
    }

    public static <T> T readJson(String str, TypeReference<T> ref) {
        try {
            return OBJECT_MAPPER.readValue(str, ref);
        } catch (IOException var3) {
            throw new RuntimeException(var3);
        }
    }
}
