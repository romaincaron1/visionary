package com.romaincaron.analyze.dto;

import com.romaincaron.analyze.enums.MediaType;
import lombok.Value;

@Value
public class MediaSimilarityResultDto {
    String mediaId;
    String title;
    MediaType mediaType;
    double similarity;
    int commonGenreCount;
    int commonTagCount;
}
