package com.romaincaron.analyze.dto;

import com.romaincaron.analyze.enums.MediaStatus;
import com.romaincaron.analyze.enums.MediaType;
import lombok.Data;

import java.util.Set;

@Data
public class MediaDto {
    private Long id;
    private String externalId;
    private String sourceName;
    private String title;
    private String titleAlternative;
    private String synopsis;
    private MediaType mediaType;
    private Integer startYear;
    private Integer endYear;
    private MediaStatus status;
    private String coverUrl;
    private Double rating;
    private Integer popularity;
    private String author;
    private String artist;
    private Set<String> genres;
    private Set<TagDto> tags;

    @Data
    public static class TagDto {
        private String name;
        private Integer relevance;
    }
}