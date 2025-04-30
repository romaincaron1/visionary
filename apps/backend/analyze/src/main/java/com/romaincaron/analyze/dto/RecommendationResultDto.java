package com.romaincaron.analyze.dto;

import com.romaincaron.analyze.entity.MediaNode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class RecommendationResultDto {
    private MediaDto media;
    private double combinedScore;
    private double graphSimilarity;
    private double vectorSimilarity;
    private Map<String, Object> explanation;
}