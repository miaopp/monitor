package com.miaopp.monitor.metrics;

import com.miaopp.monitor.constant.Measure;
import com.miaopp.monitor.constant.MonitorConst;
import com.miaopp.monitor.utils.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

/**
 * Created by miaoping on 16/12/2.
 */
public class MetricsContext {

    private static final Logger logger = LoggerFactory.getLogger(MetricsContext.class);

    //所在环境，线上 or 线下
    private static String metricsEnv;

    //数据库名
    private static String metricsDatabase;

    private static InfluxDB influxDB;

    static {
        String userName = "";
        String password = "";
        String metricUrl = "";
        PropertiesUtil props = PropertiesUtil.getProperties();
        try {
            metricsEnv = props.getStringProperty(MonitorConst.METRICS_ENV, MonitorConst.METRICS_ENV_DEFAULT);
            metricsDatabase = props.getStringProperty(MonitorConst.METRICS_DATABASE, MonitorConst.METRICS_DATABASE_DEFAULT);
            if (StringUtils.equals("prod", metricsEnv)) {
                userName = MonitorConst.USER_NAME_PROD;
                password = MonitorConst.PASSWORD_PROD;
                metricUrl = MonitorConst.METRICS_URL_PROD;
            } else {
                userName = MonitorConst.USER_NAME_DEV;
                password = MonitorConst.PASSWORD_DEV;
                metricUrl = MonitorConst.METRICS_URL_DEV;
            }
            influxDB = InfluxDBFactory.connect(metricUrl, userName, password);
            influxDB.ping();
            influxDB.enableBatch(MonitorConst.ACTIONS_DEFAULT, MonitorConst.DURATION_DEFAULT, TimeUnit.SECONDS);
            logger.info("influxdb init success, metricsUrl = {}, userName = {}, database = {}", metricUrl, userName, metricsDatabase);
        } catch (Exception e) {
            logger.error("init metrics context error", e);
            influxDB = null;
        }
    }

    public void count(String name, Integer count) {
        nativeCount(name, count);
    }

    public void time(String name, Long time) {
        nativeTime(name, time);
    }

    private void nativeCount(String name, Integer count) {
        try {
            if (null == influxDB) {
                logger.info("the influxdb connection is null.");
                return;
            }
            String measurementName = buildMeasurementName(name);
            Point point = Point.measurement(measurementName + "_count").tag("host", getHostAddress()).tag("monitor_name", StringUtils.substringAfter(name, "#") + "_count").field("count", count).build();
            influxDB.write(metricsDatabase, null, point);
            logger.info("monitor count success, name = {}, count = {}", name, count);
            //todo 报警
        } catch (Exception e) {
            logger.error("monitor count error, name = {}, count = {}", name, count);
        }
    }

    private void nativeTime(String name, Long time) {
        try {
            if (null == influxDB) {
                logger.info("the influxdb connection is null.");
                return;
            }
            String measurementName = buildMeasurementName(name);
            Point point = Point.measurement(measurementName + "_time").tag("host", getHostAddress()).tag("monitor_name", StringUtils.substringAfter(name, "#") + "_time").field("used_time", time).build();
            influxDB.write(metricsDatabase, null, point);
            logger.info("monitor time success, name = {}, time = {}", name, time);
            //todo 报警
        } catch (Exception e) {
            logger.error("monitor time error, name = {}, time = {}", name, time);
        }
    }

    private String buildMeasurementName(String name) {
        if (-1 != name.indexOf("#")) {
            String measurementName = StringUtils.substringBefore(name, "#");
            if (null != Measure.valuesOf(measurementName.toLowerCase())) {
                return measurementName;
            }
        }
        return Measure.other.name();
    }

    private String getHostAddress() {
        try {
            Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress inetAddress;
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) netInterfaces.nextElement();
                Enumeration addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    inetAddress = (InetAddress) addresses.nextElement();
                    if (null != inetAddress && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
            return "";
        } catch (SocketException e) {
            logger.error("get hostAddress error", e);
            return "";
        }
    }
}
