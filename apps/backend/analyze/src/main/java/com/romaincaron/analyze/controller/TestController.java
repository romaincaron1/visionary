package com.romaincaron.analyze.controller;

import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.repositories.MediaNodeRepository;
import com.romaincaron.analyze.service.entity.MediaNodeService;
import com.romaincaron.analyze.service.vector.VectorGenerationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/test")
@Slf4j
@AllArgsConstructor
public class TestController {

    private final VectorGenerationService vectorGenerationService;
    private final MediaNodeService mediaNodeService;
    private final MediaNodeRepository mediaNodeRepository;

    @GetMapping("/vector-stats-simple")
    public ResponseEntity<Map<String, Object>> getVectorStatsSimple() {
        Map<String, Object> result = mediaNodeService.getVectorStats();
        return ResponseEntity.ok(result);
    }
}
