package com.romaincaron.data_collection.controller;

import com.romaincaron.data_collection.dto.MediaDto;
import com.romaincaron.data_collection.service.entity.MediaService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    // Get all medias
    @GetMapping("/media/all")
    public ResponseEntity<List<MediaDto>> getAllMedia() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mediaService.findAll());
    }

    // Get media by its ID
    @GetMapping("/media/{id}")
    public ResponseEntity<MediaDto> getMediaById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mediaService.findById(id));
    }

    // Get media by its external ID and its source name
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
