package com.romaincaron.analyze.service.client;

import com.romaincaron.analyze.dto.MediaDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "data-collection")
public interface DataCollectionClient {

    @GetMapping("/api/media/")
    List<MediaDto> getAllMedia();

    @GetMapping("/api/media/{mediaId}")
    MediaDto getMediaById(@PathVariable("mediaId") Long id);

    @GetMapping("/api/media/source/{externalId}")
    MediaDto getMediaByExternalIdAndSourceName(@PathVariable("externalId") String externalId, @RequestParam String sourceName);
}