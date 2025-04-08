package com.romaincaron.analyze.service.vector;

import com.romaincaron.analyze.service.entity.GenreNodeService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreDictionaryService {

    private final GenreNodeService genreNodeService;
    private final Map<String, Integer> genreIndexMap = new ConcurrentHashMap<>();
    private AtomicInteger nextIndex = new AtomicInteger(0);

    @PostConstruct
    public void initializeDicitonary() {
        log.info("Initializing genre dictionary...");
        genreNodeService.findAll().forEach(genre -> {
            genreIndexMap.computeIfAbsent(genre.getName(), k -> nextIndex.getAndIncrement());
        });
        log.info("Genre dictionary initialized with {} genres", genreIndexMap.size());
    }

    public Map<String, Integer> getGenreIndexMap() {
        return new HashMap<>(genreIndexMap);
    }

    public int getOrCreateGenreIndex(String genreName) {
        return genreIndexMap.computeIfAbsent(genreName, k -> nextIndex.getAndIncrement());
    }

    public int getDictionarySize() {
        return genreIndexMap.size();
    }

}
