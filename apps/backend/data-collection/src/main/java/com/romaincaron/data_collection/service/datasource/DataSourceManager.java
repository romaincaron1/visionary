package com.romaincaron.data_collection.service.datasource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataSourceManager {
    private final List<DataSource> dataSources;

    public Optional<DataSource> getDataSourceByName(String sourceName) {
        return dataSources.stream()
                .filter(source -> source.getSourceName().equals(sourceName))
                .findFirst();
    }

    public List<DataSource> getAvailableSources() {
        return dataSources.stream()
                .filter(DataSource::isAvailable)
                .collect(Collectors.toList());
    }
}
