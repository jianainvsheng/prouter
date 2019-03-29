package com.source.sdk.prouter;

import com.alibaba.fastjson.JSON;

/**
 * Created by yangjian on 2018/5/17.
 */

public class JSONUtil {
    public JSONUtil() {
    }

    public static String object2Json(Object instance) {
        return JSON.toJSONString(instance);
    }

    public static <T> T json2Object(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz);
    }
}
