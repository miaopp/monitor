package com.miaopp.monitor.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by miaoping on 16/12/4.
 */
public enum Measure {
    http,//http接口
    dubbo,//dubbo接口
    service,//service层接口
    database,//数据库相关
    error,//异常
    other;//其他

    public static Measure valuesOf(String value) {
        for (Measure measure : Measure.values()) {
            if (StringUtils.equals(value, measure.name())) {
                return measure;
            }
        }
        return null;
    }
}
