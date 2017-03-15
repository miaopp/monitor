package com.miaopp.monitor.utils;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by miaoping on 16/12/7.
 */
public class JsonUtil {

    private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private static volatile ObjectMapper objectMapper = new ObjectMapper();

    public static String serialize(Object object) {
        if (null == object) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            logger.error("json serialized error", e);
            return null;
        }
    }

    public static <T> T jsonDecode(String json, Class<T> valueType) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, valueType);
        } catch (Exception e) {
            logger.error("json decode error", e);
            return null;
        }
    }
}
