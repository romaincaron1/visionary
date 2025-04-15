package com.romaincaron.analyze.controller;

import com.romaincaron.analyze.dto.RecommendationResultDto;
import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.service.Neo4jSynchronizationService;
import com.romaincaron.analyze.service.RecommendationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class SynchronizationController {

    private final Neo4jSynchronizationService synchronizationService;
    private final RecommendationService recommendationService;

    @GetMapping("/sync/{mediaId}")
    public ResponseEntity<MediaNode> syncMedia(@PathVariable long mediaId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(synchronizationService.syncMediaToNeo4j(mediaId));
    }

    @GetMapping("/test/{mediaId}")
    public ResponseEntity<List<RecommendationResultDto>> test(@PathVariable String mediaId) throws Exception {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(recommendationService.getRecommendations(mediaId, "anilist", 5));
    }

}
