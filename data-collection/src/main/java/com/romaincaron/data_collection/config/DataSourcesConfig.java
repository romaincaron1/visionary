package com.romaincaron.data_collection.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "datasources")
public record DataSourcesConfig(Map<String, DataSourceConfig> sources) {

    public record DataSourceConfig(
            String url,
            String apiKey,
            int timeout
    ) {}

    public DataSourceConfig getSource(String name) {
        return sources.get(name);
    }
}