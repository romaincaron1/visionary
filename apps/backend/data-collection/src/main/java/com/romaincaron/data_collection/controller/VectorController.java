package com.romaincaron.data_collection.controller;

import com.romaincaron.data_collection.entity.MediaVector;
import com.romaincaron.data_collection.service.entity.MediaVectorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/vectors")
public class VectorController {

    private final MediaVectorService mediaVectorService;

    @GetMapping("/{mediaId}")
    public ResponseEntity<MediaVector> getByMediaId(@PathVariable("mediaId") Long mediaId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mediaVectorService.findByMediaId(mediaId));
    }

    @PostMapping
    public ResponseEntity<MediaVector> saveMediaVector(@RequestBody MediaVector mediaVector) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mediaVectorService.save(mediaVector));
    }

}
