package com.romaincaron.analyze.dto;

import com.romaincaron.analyze.enums.MediaType;
import lombok.Data;

import java.util.List;

@Data
public class RecommendationRequestDto {
    private List<String> mediaIds;
    private MediaType mediaType;
} 