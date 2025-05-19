package com.romaincaron.analyze.controller;

import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.repository.MediaNodeRepository;
import com.romaincaron.analyze.service.entity.MediaNodeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
@Slf4j
@AllArgsConstructor
public class TestController {

    private final MediaNodeService mediaNodeService;
    private final MediaNodeRepository mediaNodeRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Nombre total de médias
        long totalMedia = mediaNodeRepository.count();
        stats.put("totalMedia", totalMedia);
        
        // Nombre de médias par type
        List<Map<String, Object>> mediaTypeCounts = mediaNodeRepository.countByMediaType();
        Map<String, Long> mediaByType = new HashMap<>();
        for (Map<String, Object> count : mediaTypeCounts) {
            String type = (String) count.get("type");
            Number countValue = (Number) count.get("count");
            mediaByType.put(type, countValue.longValue());
        }
        stats.put("mediaByType", mediaByType);
        
        // Nombre moyen de tags par média
        double avgTags = mediaNodeRepository.getAverageTagCount();
        stats.put("averageTagsPerMedia", avgTags);
        
        // Nombre moyen de genres par média
        double avgGenres = mediaNodeRepository.getAverageGenreCount();
        stats.put("averageGenresPerMedia", avgGenres);
        
        return ResponseEntity.ok(stats);
    }
}
