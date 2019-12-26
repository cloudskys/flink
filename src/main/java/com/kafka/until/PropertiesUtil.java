package com.kafka.until;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Description:
 * @Author: sunxuefeng
 * @Time: 2019/4/8 10:29
 * @Version: 1.0
 */
public class PropertiesUtil {
    private static Map<String, String> resourceRecordMap = new HashMap();
    private static Properties properties = new Properties();

    public PropertiesUtil() {
    }

    public static synchronized Properties loadPropertiesFile(String fileName) throws IOException {
        if (resourceRecordMap.get(fileName) == null) {
            InputStream is = null;

            try {
                is = PropertiesUtil.class.getResourceAsStream(fileName);
                properties.load(is);
                PropertiesExpressionParser expParser = PropertiesExpressionParser.getInstance();
                if (properties.get("proKeyPath")!=null){
                    expParser.setProKeyPath(properties.get("proKeyPath").toString());
                    String k;
                    // 解析配置文件
                    for (Object key : properties.keySet()) {
                        k = key.toString();
                       // properties.put(k, expParser.parse(k, properties.getProperty(k)));
                    }
                }
                resourceRecordMap.put(fileName, "");
                is.close();
            } finally {
                if (is != null) {
                    is.close();
                }
            }

        }
        return properties;
    }

    public static String getConfigValue(String value) {
        return getConfigValue(value, null);
    }

    public static synchronized String getConfigValue(String value, String defaultValue) {
        return properties.getProperty(value) == null ? defaultValue : properties.getProperty(value).trim();
    }

    public static synchronized int getConfigValue(String value, int defaultValue) {
        return properties.getProperty(value) == null ? defaultValue : Integer.parseInt(properties.getProperty(value).trim());
    } public static synchronized boolean getConfigValue(String value, boolean defaultValue) {
        return properties.getProperty(value) == null ? defaultValue : Boolean.parseBoolean(properties.getProperty(value).trim());
    }
}

