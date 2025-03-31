package com.romaincaron.data_collection.controller;

import com.romaincaron.data_collection.entity.Media;
import com.romaincaron.data_collection.service.entity.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaService mediaService;

    @Autowired
    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Media>> getAllMedia() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mediaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Media> getMediaById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mediaService.findById(id));
    }

}
