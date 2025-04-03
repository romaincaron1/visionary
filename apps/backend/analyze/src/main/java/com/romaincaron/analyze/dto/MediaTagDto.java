package com.romaincaron.analyze.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaTagDto {
    private TagDto tag;
    private Integer relevance;
    private String confidenceLevel;
}