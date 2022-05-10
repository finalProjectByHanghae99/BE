package com.hanghae99.finalproject.post.model;

import java.util.HashMap;
import java.util.Map;

public class SuccessResult {

    public static Map<String, Object> success(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", data);
        return result;
    }
}