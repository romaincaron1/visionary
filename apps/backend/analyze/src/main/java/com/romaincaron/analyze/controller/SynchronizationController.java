package com.romaincaron.analyze.controller;

import com.romaincaron.analyze.dto.MediaDto;
import com.romaincaron.analyze.dto.RecommendationResultDto;
import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.mapper.MediaMapper;
import com.romaincaron.analyze.service.Neo4jSynchronizationService;
import com.romaincaron.analyze.service.RecommendationService;
import com.romaincaron.analyze.service.client.DataCollectionClient;
import com.romaincaron.analyze.service.entity.MediaNodeService;
import com.romaincaron.analyze.service.vector.VectorGenerationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@Slf4j
@AllArgsConstructor
public class SynchronizationController {

    private final Neo4jSynchronizationService synchronizationService;
    private final MediaNodeService mediaNodeService;
    private final RecommendationService recommendationService;
    private final VectorGenerationService vectorGenerationService;
    private final DataCollectionClient dataCollectionClient;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private Map<Long, Integer> vectors = new HashMap<>();


    @GetMapping("/test/{mediaId}")
    public ResponseEntity<List<RecommendationResultDto>> test(@PathVariable String mediaId) throws Exception {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(recommendationService.getRecommendations(mediaId, "anilist", 5));
    }

    @GetMapping("test/get/{mediaNodeId}")
    public ResponseEntity<MediaNode> getMedia(@PathVariable Long mediaNodeId) throws Exception {
        Optional<MediaNode> mediaNode = mediaNodeService.findById(mediaNodeId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mediaNode.get());
    }

    @GetMapping("/test/allvector")
    public ResponseEntity<String> generateAllVectors() throws Exception {
        vectorGenerationService.generateAllVectors();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("ok");
    }

    @GetMapping("/syncone/{externalId}")
    public ResponseEntity<MediaDto> syncone(@PathVariable String externalId, @RequestParam String sourceName) throws Exception {
        MediaDto mediaDto = dataCollectionClient.getMediaByExternalIdAndSourceName(externalId, sourceName);
        synchronizationService.syncMediaToNeo4j(mediaDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mediaDto);
    }
}
