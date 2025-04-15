package com.romaincaron.data_collection.controller;

import com.romaincaron.data_collection.dto.MediaData;
import com.romaincaron.data_collection.dto.MediaDto;
import com.romaincaron.data_collection.dto.SyncResult;
import com.romaincaron.data_collection.enums.MediaType;
import com.romaincaron.data_collection.service.entity.MediaService;
import com.romaincaron.data_collection.service.synchronization.MediaSynchronizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MediaController {

    private final MediaService mediaService;
    private final MediaSynchronizationService mediaSynchronizationService;

    @Autowired
    public MediaController(MediaService mediaService, MediaSynchronizationService mediaSynchronizationService) {
        this.mediaService = mediaService;
        this.mediaSynchronizationService = mediaSynchronizationService;
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
}
