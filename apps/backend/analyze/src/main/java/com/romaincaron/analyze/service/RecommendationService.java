package com.romaincaron.analyze.service;

import com.romaincaron.analyze.dto.MediaDto;
import com.romaincaron.analyze.dto.MediaSimilarityResultDto;
import com.romaincaron.analyze.dto.RecommendationResultDto;
import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.enums.MediaType;
import com.romaincaron.analyze.mapper.MediaMapper;
import com.romaincaron.analyze.service.entity.MediaNodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationService {

    private final MediaSimilarityService mediaSimilarityService;
    private final MediaNodeService mediaNodeService;
    private static final int MAX_RECOMMENDATIONS = 10;

    public List<RecommendationResultDto> getRecommendations(
            String mediaId,
            MediaType mediaType) throws Exception {
        
        // Récupérer le média source
        MediaNode sourceMedia = mediaNodeService.findByExternalId(mediaId)
                .orElseThrow(() -> new Exception("Media not found: " + mediaId));

        if (log.isInfoEnabled()) {
            log.info("Finding recommendations for media: {}", sourceMedia.getTitle());
        }

        // Obtenir les recommandations
        List<MediaSimilarityResultDto> results = mediaSimilarityService.findSimilarMedia(
            sourceMedia,
            MAX_RECOMMENDATIONS,
            mediaType
        );

        // Convertir les résultats
        return results.stream()
            .map(result -> {
                MediaNode media = mediaNodeService.findByExternalId(result.getMediaId())
                    .orElse(null);
                if (media == null) return null;

                MediaDto mediaDto = MediaMapper.MAP_TO_MEDIA_DTO(media);
                Map<String, Object> explanation = new HashMap<>();
                explanation.put("similarity", Math.round(result.getSimilarity() * 100) / 100.0);
                explanation.put("commonGenres", result.getCommonGenreCount());
                explanation.put("commonTags", result.getCommonTagCount());

                return new RecommendationResultDto(
                    mediaDto,
                    result.getSimilarity(),
                    result.getSimilarity(),
                    result.getSimilarity(),
                    explanation
                );
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
