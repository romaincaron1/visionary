package com.romaincaron.data_collection.controller;

import com.romaincaron.data_collection.dto.MediaDto;
import com.romaincaron.data_collection.dto.SyncResult;
import com.romaincaron.data_collection.enums.MediaType;
import com.romaincaron.data_collection.service.synchronization.MediaSynchronizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class MediaSyncController {

    private final MediaSynchronizationService syncService;

    @GetMapping("/all")
    public ResponseEntity<Map<MediaType, SyncResult>> synchronizeAllMedia() {
        return ResponseEntity.ok(syncService.synchronizeAllMediaTypes());
    }

    @GetMapping("/type/{mediaType}")
    public ResponseEntity<SyncResult> synchronizeMediaByType(@PathVariable MediaType mediaType) {
        return ResponseEntity.ok(syncService.synchronizeMediaByType(mediaType));
    }

    @GetMapping("/media/{externalId}")
    public ResponseEntity<MediaDto> synchronizeMedia(
            @PathVariable String externalId,
            @RequestParam MediaType mediaType,
            @RequestParam String sourceName) {
        return ResponseEntity.ok(syncService.synchronizeMediaById(externalId, mediaType, sourceName));
    }

}
