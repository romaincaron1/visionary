package com.romaincaron.analyze.dto;

import com.romaincaron.analyze.enums.MediaStatus;
import com.romaincaron.analyze.enums.MediaType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
public class MediaDto {
    private Long id;
    private String title;
    private String externalId;
    private String titleAlternative;
    private MediaType mediaType;
    private String synopsis;
    private String author;
    private String artist;
    private Integer startYear;
    private Integer endYear;
    private MediaStatus status;
    private String coverUrl;
    private Double rating;
    private Integer popularity;
    private String sourceName;
    private LocalDateTime lastUpdated;
    private Set<GenreDto> genres = new HashSet<>();
    private Set<MediaTagDto> mediaTags = new HashSet<>();
    private String checksum;
}
