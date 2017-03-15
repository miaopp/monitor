package com.miaopp.monitor.utils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Created by miaoping on 16/12/6.
 * 读取配置文件
 */
public class PropertiesUtil {

    private static final Logger logger = LoggerFactory.getLogger(Properties.class);

    private static final PropertiesUtil MONITOR_PROPERTIES = new PropertiesUtil("/monitor/monitor.properties");

    private Properties props;

    public PropertiesUtil() {

    }

    public PropertiesUtil(final Properties props) {
        this.props = props;
    }

    private PropertiesUtil(final String filePath) {
        Properties props = new Properties();
        InputStream in = null;
        try {
            in = PropertiesUtil.class.getResourceAsStream(filePath);
            if (null != in) {
                props.load(in);
            }
        } catch (Exception e) {
            logger.error("load properties error", e);
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                logger.error("close properties input stream error", e);
            }
        }
        this.props = props;
    }

    public static PropertiesUtil getProperties() {
        return MONITOR_PROPERTIES;
    }

    public String getStringProperty(final String name, final String defaultValue) {
        return props.getProperty(name, defaultValue);
    }

    public int getIntProperty(final String name, final int defaultValue) {
        String value = props.getProperty(name, "");
        try {
            return StringUtils.isBlank(value) ? defaultValue : Integer.valueOf(value);
        } catch (Exception e) {
            logger.error("get int property error, key = {}", name, e);
            return defaultValue;
        }
    }

    public List<String> getListProperty(final String name) {
        String values = getStringProperty(name, "");
        return StringUtils.isBlank(values) ? Lists.<String>newArrayList() : Splitter.on(",").omitEmptyStrings().trimResults().splitToList(values);
    }
}
