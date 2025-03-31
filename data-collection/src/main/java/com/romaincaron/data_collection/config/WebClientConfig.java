package com.romaincaron.data_collection.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(DataSourcesConfig.class)
public class WebClientConfig {

    private final DataSourcesConfig dataSourcesConfig;

    public WebClientConfig(DataSourcesConfig dataSourcesConfig) {
        this.dataSourcesConfig = dataSourcesConfig;
    }

    @Bean
    public Map<String, WebClient> webClients() {
        Map<String, WebClient> clients = new HashMap<>();

        dataSourcesConfig.sources().forEach((name, config) -> {
            WebClient.Builder builder = WebClient.builder()
                    .baseUrl(config.url())
                    .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024));

            builder = builder.filter((request, next) ->
                    next.exchange(request)
                            .timeout(Duration.ofMillis(config.timeout())));

            if (config.apiKey() != null && !config.apiKey().isEmpty()) {
                builder = builder.defaultHeader("Authorization", "Bearer " + config.apiKey());
            }

            clients.put(name, builder.build());
        });

        return clients;
    }
}