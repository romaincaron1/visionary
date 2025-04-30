package com.romaincaron.analyze.service;

import com.romaincaron.analyze.dto.MediaDto;
import com.romaincaron.analyze.dto.MediaSimilarityResultDto;
import com.romaincaron.analyze.dto.RecommendationResultDto;
import com.romaincaron.analyze.entity.MediaNode;
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

    public List<RecommendationResultDto> getRecommendations(String mediaId, String sourceName, int limit) throws Exception {
        MediaNode sourceMedia = mediaNodeService.findByExternalIdAndSourceName(mediaId, sourceName)
                .orElseThrow(() -> new Exception("Media not found: " + mediaId));

        log.info("Finding recommendations for media: {} (ID: {})", sourceMedia.getTitle(), mediaId);

        int extendedLimit = Math.min(limit * 3, 50);

        List<MediaSimilarityResultDto> graphResults =
                mediaSimilarityService.findSimilarMedia(mediaId, sourceName, extendedLimit);

        log.info("Found {} similar media using Neo4j graph approach", graphResults.size());

        if (graphResults.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, RecommendationResultDto> recommendations = new HashMap<>();
        for (MediaSimilarityResultDto graphResult : graphResults) {
            try {
                MediaNode targetMedia = mediaNodeService.findByExternalIdAndSourceName(graphResult.getMediaId(), graphResult.getSourceName())
                        .orElseThrow(() -> new Exception("Media not found: " + graphResult.getMediaId()));

                double[] sourceVector = sourceMedia.getVectorAsDoubleArray();
                double[] targetVector = targetMedia.getVectorAsDoubleArray();

                System.out.println(
                        Arrays.toString(sourceVector) + "\n" + Arrays.toString(targetVector)
                );

                double vectorSimilarity = 0.0;
                if (sourceVector.length > 0 && targetVector.length > 0) {
                    vectorSimilarity = mediaSimilarityService.calculateCosineSimilarity(sourceMedia, targetMedia);
                }

                double graphWeight = 0.5;
                double vectorWeight = 0.5;

                if (sourceVector.length == 0 || targetVector.length == 0) {
                    graphWeight = 1.0;
                    vectorWeight = 0.0;
                }

                double combinedScore = graphResult.getSimilarity() * graphWeight + vectorSimilarity * vectorWeight;

                MediaDto mediaDto = MediaMapper.MAP_TO_MEDIA_DTO(targetMedia);

                RecommendationResultDto recommendation = new RecommendationResultDto(
                        mediaDto,
                        combinedScore,
                        graphResult.getSimilarity(),
                        vectorSimilarity,
                        createExplanation(graphResult, vectorSimilarity)
                );

                recommendations.put(graphResult.getMediaId(), recommendation);

            } catch (Exception e) {
                log.error("Error processing recommendation for media {}: {}",
                        graphResult.getMediaId(), e.getMessage());
            }
        }

        return recommendations.values().stream()
                .sorted(Comparator.comparing(RecommendationResultDto::getCombinedScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private Map<String, Object> createExplanation(
            MediaSimilarityResultDto graphResult,
            double vectorSimilarity) {

        Map<String, Object> explanation = new HashMap<>();
        explanation.put("graphSimilarity", Math.round(graphResult.getSimilarity() * 100) / 100.0);
        explanation.put("vectorSimilarity", Math.round(vectorSimilarity * 100) / 100.0);
        explanation.put("commonGenres", graphResult.getCommonGenreCount());
        explanation.put("commonTags", graphResult.getCommonTags());

        return explanation;
    }

}
