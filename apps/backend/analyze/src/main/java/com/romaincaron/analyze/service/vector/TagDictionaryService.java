package com.romaincaron.analyze.service.vector;

import com.romaincaron.analyze.service.entity.TagNodeService;
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
public class TagDictionaryService {

    private final TagNodeService tagNodeService;
    private final Map<String, Integer> tagIndexMap = new ConcurrentHashMap<>();
    private AtomicInteger nextIndex = new AtomicInteger(0);

    @PostConstruct
    public void initializeDictionary() {
        log.info("Initializing tag dictionary");
        tagNodeService.findAll().forEach(tagNode -> {
            tagIndexMap.computeIfAbsent(tagNode.getName(), k -> nextIndex.getAndIncrement());
        });
        log.info("Tag dictionary initalized with {} tags", tagIndexMap.size());
    }

    public Map<String, Integer> getTagIndexMap() {
        return new HashMap<>(tagIndexMap);
    }

    public int getOrCreateTagIndex(String tagName) {
        return tagIndexMap.computeIfAbsent(tagName, k -> nextIndex.getAndIncrement());
    }

    public int getDictionarySize() {
        return tagIndexMap.size();
    }

}
