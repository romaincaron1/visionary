package com.romaincaron.analyze.controller;

import com.romaincaron.analyze.dto.RecommendationResultDto;
import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.mapper.MediaMapper;
import com.romaincaron.analyze.service.Neo4jSynchronizationService;
import com.romaincaron.analyze.service.RecommendationService;
import com.romaincaron.analyze.service.entity.MediaNodeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class SynchronizationController {

    private final Neo4jSynchronizationService synchronizationService;
    private final MediaNodeService mediaNodeService;
    private final RecommendationService recommendationService;


    @GetMapping("/test/{mediaId}")
    public ResponseEntity<List<RecommendationResultDto>> test(@PathVariable String mediaId) throws Exception {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(recommendationService.getRecommendations(mediaId, "anilist", 5));
    }

}
