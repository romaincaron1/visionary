package com.romaincaron.data_collection.mapper;

import com.romaincaron.data_collection.dto.MediaData;
import com.romaincaron.data_collection.entity.enums.MediaStatus;
import com.romaincaron.data_collection.entity.enums.MediaType;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.romaincaron.data_collection.service.datasource.AniListDataSource.SOURCE_NAME;

public class MediaDataMapper {
    public static MediaData MAP_TO_MEDIADATA(Map<String, Object> response) {
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        if (data == null) {
            throw new IllegalArgumentException("Invalid response format: missing 'data' field");
        }

        Map<String, Object> media = (Map<String, Object>) data.get("Media");
        if (media == null) {
            throw new IllegalArgumentException("Invalid response format: missing 'Media' field");
        }

        Map<String, String> titles = (Map<String, String>) media.get("title");
        if (titles == null) {
            throw new IllegalArgumentException("Invalid response format: missing 'title' field");
        }

        String title = titles.get("romaji");
        String titleAlternative = titles.get("english") != null ? titles.get("english") : titles.get("native");

        Integer startYear = ExtractYear(media.get("startDate"));
        Integer endYear = ExtractYear(media.get("endDate"));

        String coverUrl = null;
        if (media.get("coverImage") != null) {
            coverUrl = (String) ((Map<String, Object>) media.get("coverImage")).get("large");
        }

        MediaStatus status = MapStatus((String) media.get("status"));

        // Handle genres with null safety
        Set<String> genres;
        if (media.get("genres") instanceof Set) {
            genres = (Set<String>) media.get("genres");
        } else if (media.get("genres") instanceof List) {
            genres = new HashSet<>((List<String>) media.get("genres"));
        } else {
            genres = Set.of();
        }

        Set<MediaData.TagData> tags = ExtractTags(media.get("tags"));

        return MediaData.builder()
                .externalId(String.valueOf(media.get("id")))
                .sourceName(SOURCE_NAME)
                .title(title)
                .titleAlternative(titleAlternative)
                .synopsis((String) media.get("description"))
                .mediaType(MediaType.MANGA)
                .startYear(startYear)
                .endYear(endYear)
                .status(status)
                .coverUrl(coverUrl)
                .rating(media.get("meanScore") != null ?
                        ((Number) media.get("meanScore")).doubleValue() / 10 : null)
                .popularity(media.get("popularity") != null ?
                        ((Number) media.get("popularity")).intValue() : null)
                .genres(genres)
                .tags(tags)
                .build();
    }

    public static Integer ExtractYear(Object dateObject) {
        if (dateObject == null) return null;

        Map<String, Object> date = (Map<String, Object>) dateObject;
        Object year = date.get("year");
        return year != null ? ((Number) year).intValue() : null;
    }

    public static Set<MediaData.TagData> ExtractTags(Object tagsObject) {
        if (tagsObject == null) return Set.of();

        try {
            if (tagsObject instanceof Set) {
                Set<Map<String, Object>> tagsSet = (Set<Map<String, Object>>) tagsObject;
                return tagsSet.stream()
                        .map(tag -> MediaData.TagData.builder()
                                .name((String) tag.get("name"))
                                .relevance(tag.get("rank") != null ?
                                        ((Number) tag.get("rank")).intValue() : null)
                                .build())
                        .collect(Collectors.toSet());
            } else if (tagsObject instanceof List) {
                List<Map<String, Object>> tagsList = (List<Map<String, Object>>) tagsObject;
                return tagsList.stream()
                        .map(tag -> MediaData.TagData.builder()
                                .name((String) tag.get("name"))
                                .relevance(tag.get("rank") != null ?
                                        ((Number) tag.get("rank")).intValue() : null)
                                .build())
                        .collect(Collectors.toSet());
            }
        } catch (Exception e) {
            // If there's any error processing tags, log and return empty set rather than failing
            System.err.println("Error extracting tags: " + e.getMessage());
        }

        return Set.of();
    }

    public static MediaStatus MapStatus(String anilistStatus) {
        if (anilistStatus == null) return MediaStatus.UNKNOWN;

        return switch (anilistStatus) {
            case "FINISHED" -> MediaStatus.COMPLETED;
            case "RELEASING" -> MediaStatus.ONGOING;
            case "NOT_YET_RELEASED" -> MediaStatus.UPCOMING;
            case "CANCELLED" -> MediaStatus.CANCELLED;
            case "HIATUS" -> MediaStatus.HIATUS;
            default -> MediaStatus.UNKNOWN;
        };
    }
}