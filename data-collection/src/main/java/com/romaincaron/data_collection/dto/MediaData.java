package com.romaincaron.data_collection.dto;

import com.romaincaron.data_collection.entity.enums.MediaStatus;
import com.romaincaron.data_collection.entity.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaData {
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
    private Set<TagData> tags;

    @Getter
    @Builder
    public static class TagData {
        private String name;
        private Integer relevance; // 0-100
    }
}
