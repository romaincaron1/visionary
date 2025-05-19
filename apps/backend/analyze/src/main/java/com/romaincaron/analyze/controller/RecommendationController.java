package com.romaincaron.analyze.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.romaincaron.analyze.dto.RecommendationRequestDto;
import com.romaincaron.analyze.dto.RecommendationResultDto;
import com.romaincaron.analyze.service.RecommendationService;
import com.romaincaron.analyze.enums.MediaType;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{mediaId}")
    public ResponseEntity<List<RecommendationResultDto>> getRecommendations(
            @PathVariable String mediaId,
            @RequestParam(required = true) MediaType mediaType) throws Exception {
        return ResponseEntity.ok(recommendationService.getRecommendations(mediaId, mediaType));
    }
}
