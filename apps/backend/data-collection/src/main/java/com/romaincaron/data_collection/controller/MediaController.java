package com.romaincaron.data_collection.controller;

import com.romaincaron.data_collection.dto.MediaDto;
import com.romaincaron.data_collection.service.entity.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MediaController {

    private final MediaService mediaService;

    @Autowired
    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping("/media/all")
    public ResponseEntity<List<MediaDto>> getAllMedia() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mediaService.findAll());
    }

    @GetMapping("/media/{id}")
    public ResponseEntity<MediaDto> getMediaById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mediaService.findById(id));
    }

    @GetMapping("/media/source/{externalId}")
    public ResponseEntity<MediaDto> getMediaByExternalIdAndSourceName(
            @PathVariable("externalId") String externalId,
            @RequestParam String sourceName) {
        MediaDto mediaDto = mediaService.findByExternalIdAndSourceName(externalId, sourceName)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve synchronized media"));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mediaDto);
    }
}
