package com.miaopp.monitor.metrics;

import com.miaopp.monitor.constant.Measure;
import com.miaopp.monitor.constant.MonitorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by miaoping on 16/12/5.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Monitorable {
    //监控类型，默认记录接口调用次数+响应时间
    MonitorType type() default MonitorType.time_count;
    //measure -> mysql table，对应具体的表
    Measure measure() default Measure.other;
}
