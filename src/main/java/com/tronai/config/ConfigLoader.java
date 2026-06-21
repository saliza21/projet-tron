package com.tronai.config;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static Properties config = new Properties();

    static {
        try {
            InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties");
            if (inputStream != null) {
                config.load(inputStream);
            } else {
                throw new RuntimeException("Config file 'config.properties' not found!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading config file: " + e.getMessage(), e);
        }
    }

    public static String getProperty(String key) {
        return config.getProperty(key);
    }

    public static int getIntProperty(String key) {
        String value = getProperty(key);
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Missing or invalid property: " + key);
        }
        return Integer.parseInt(value);
    }
}