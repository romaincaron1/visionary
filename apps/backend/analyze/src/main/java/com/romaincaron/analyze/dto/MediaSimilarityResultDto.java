package com.romaincaron.analyze.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class MediaSimilarityResultDto {
    private String mediaId;
    private String sourceName;
    private String title;
    private String mediaType;
    private double similarity;
    private int commonGenreCount;
    private double genreSimilarity;
    private int commonTagCount;
    private double tagSimilarity;
    private List<String> commonTags;

    public Map<String, Object> getExplanation() {
        Map<String, Object> explanation = new HashMap<>();
        explanation.put("similarityScore", Math.round(similarity * 100) / 100.0);
        explanation.put("commonGenres", commonGenreCount);
        explanation.put("commonTags", commonTags);
        return explanation;
    }
}
