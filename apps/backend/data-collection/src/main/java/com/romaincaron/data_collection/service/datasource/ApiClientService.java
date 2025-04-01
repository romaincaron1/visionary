package com.romaincaron.data_collection.service.datasource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApiClientService {
    private final Map<String, WebClient> webClients;

    public WebClient getClient(String sourceName) {
        WebClient client = webClients.get(sourceName);
        if (client == null) {
            throw new IllegalArgumentException("No API client configured for source: " + sourceName);
        }
        return client;
    }

    public <T> T executeGet(String sourceName, String path, Class<T> responseType) {
        return getClient(sourceName)
                .get()
                .uri(path)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    public <T> T executePost(String sourceName, String path, Object body, Class<T> responseType) {
        return getClient(sourceName)
                .post()
                .uri(path)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    public Map<String, Object> executeGraphQL(String sourceName, String query, Map<String, Object> variables) {
        Map<String, Object> requestBody = Map.of(
                "query", query,
                "variables", variables
        );

        return executePost(sourceName, "", requestBody, Map.class);
    }
}
