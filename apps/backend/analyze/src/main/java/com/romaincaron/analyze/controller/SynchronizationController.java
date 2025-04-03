package com.romaincaron.analyze.controller;

import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.service.Neo4jSynchronizationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class SynchronizationController {

    private final Neo4jSynchronizationService synchronizationService;

    @GetMapping("/sync/{mediaId}")
    public ResponseEntity<MediaNode> syncMedia(@PathVariable long mediaId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(synchronizationService.syncMediaToNeo4j(mediaId));
    }

}
