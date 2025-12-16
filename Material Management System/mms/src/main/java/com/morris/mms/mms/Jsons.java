package com.morris.mms.mms;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Jsons {
    private static final ObjectMapper M = new ObjectMapper();

    public static <T> T fromJson(String json, Class<T> cls) {
        try {
            return M.readValue(json, cls);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON: " + json, e);
        }
    }
}
