package com.miaopp.monitor.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by miaoping on 16/12/5.
 */
public class Monitor {

    private static final Logger logger = LoggerFactory.getLogger(Monitor.class);

    private static MetricsContext metricsContext = new MetricsContext();

    public static void recordOne(String name) {
        metricsContext.count(name, 1);
    }

    public static void recordOne(String name, Long time) {
        //记录调用次数
        metricsContext.count(name, 1);
        //记录响应时间
        metricsContext.time(name, time);
    }

    public static void recordTime(String name, Long time) {
        metricsContext.time(name, time);
    }
}
