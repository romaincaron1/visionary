package com.romaincaron.data_collection.service.datasource;

import com.romaincaron.data_collection.dto.MediaData;
import com.romaincaron.data_collection.enums.MediaType;
import com.romaincaron.data_collection.util.AniListQueries;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.romaincaron.data_collection.mapper.MediaDataMapper.MAP_TO_MEDIADATA;

@Service
@Slf4j
@RequiredArgsConstructor
public class AniListDataSource implements DataSource {

    public static final String SOURCE_NAME = "anilist";

    private final ApiClientService apiClientService;

    @Override
    public String getSourceName() {
        return SOURCE_NAME;
    }

    @Override
    public Optional<MediaData> fetchMediaById(String externalId, MediaType mediaType) {
        Map<String, Object> variables = Map.of(
                "id", Integer.parseInt(externalId),
                "type", mediaType
        );

        try {
            Map<String, Object> response = apiClientService.executeGraphQL(
                    SOURCE_NAME,
                    AniListQueries.GET_MEDIA_BY_ID,
                    variables
            );
            return Optional.of(MAP_TO_MEDIADATA(response));
        } catch (Exception e) {
            log.error("Error fetching media with ID {}: {}", externalId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<MediaData> fetchRecentlyUpdatedMedia(MediaType mediaType, int limit, int page) {
        Map<String, Object> variables = Map.of(
                "page", page,
                "perPage", limit,
                "type", mediaType,
                "sort", List.of("UPDATED_AT_DESC")
        );

        try {
            Map<String, Object> response = apiClientService.executeGraphQL(
                    SOURCE_NAME,
                    AniListQueries.GET_RECENT_MEDIA,
                    variables
            );

            return extractMediaListFromResponse(response);
        } catch (Exception e) {
            log.error("Error fetching recent media: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<MediaData> fetchAllMedia(MediaType mediaType) {
        List<MediaData> allMedia = new ArrayList<>();
        int currentPage = 1;
        boolean hasNextPage = true;
        int perPage = 50;

        log.info("Starting to fetch all media from AniList of type {}", mediaType);

        try {
            while (hasNextPage) {
                log.info("Fetching page {} of {} media", currentPage, mediaType);

                Map<String, Object> variables = Map.of(
                        "page", currentPage,
                        "perPage", perPage,
                        "type", mediaType,
                        "sort", List.of("ID")
                );

                try {
                    Map<String, Object> response = apiClientService.executeGraphQL(
                            SOURCE_NAME,
                            AniListQueries.GET_MEDIA_PAGE,
                            variables
                    );

                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data == null) {
                        log.error("Invalid API response: missing 'data' field");
                        break;
                    }

                    Map<String, Object> pageInfo = extractPageInfo(data);
                    List<MediaData> pageMedias = extractMediasFromResponse(data);

                    allMedia.addAll(pageMedias);

                    hasNextPage = (boolean) pageInfo.get("hasNextPage");
                    currentPage++;

                    log.info("Fetched {} media from page {}. Total so far: {}. Has next page: {}",
                            pageMedias.size(), currentPage - 1, allMedia.size(), hasNextPage);

                    // Pause to respect API limitations
                    if (hasNextPage) {
                        Thread.sleep(1000);
                    }

                } catch (Exception e) {
                    log.error("Error fetching page {}: {}", currentPage, e.getMessage());
                    if (currentPage == 1) {
                        return List.of();
                    } else {
                        log.warn("Returning partial results due to error (total: {})", allMedia.size());
                        return allMedia;
                    }
                }
            }

            log.info("Completed fetching all media from AniList. Total: {}", allMedia.size());
            return allMedia;

        } catch (Exception e) {
            log.error("Unexpected error fetching all media: {}", e.getMessage());
            return allMedia;
        }
    }

    private Map<String, Object> extractPageInfo(Map<String, Object> data) {
        Map<String, Object> page = (Map<String, Object>) data.get("Page");
        if (page == null) {
            throw new IllegalArgumentException("Invalid response: missing 'Page' field");
        }

        return (Map<String, Object>) page.get("pageInfo");
    }

    private List<MediaData> extractMediasFromResponse(Map<String, Object> data) {
        Map<String, Object> page = (Map<String, Object>) data.get("Page");
        List<Map<String, Object>> mediaItems = (List<Map<String, Object>>) page.get("media");

        return mediaItems.stream()
                .map(mediaItem -> {
                    // Transformer en structure attendue par MediaDataMapper
                    Map<String, Object> mediaResponse = new HashMap<>();
                    Map<String, Object> mediaContainer = new HashMap<>();

                    // Convertir la liste des genres en Set si nécessaire
                    if (mediaItem.containsKey("genres") && mediaItem.get("genres") instanceof List) {
                        List<String> genresList = (List<String>) mediaItem.get("genres");
                        Set<String> genresSet = new HashSet<>(genresList);
                        mediaItem.put("genres", genresSet);
                    }

                    // Convertir la liste des tags en Set si nécessaire
                    if (mediaItem.containsKey("tags") && mediaItem.get("tags") instanceof List) {
                        List<Map<String, Object>> tagsList = (List<Map<String, Object>>) mediaItem.get("tags");
                        Set<Map<String, Object>> tagsSet = new HashSet<>(tagsList);
                        mediaItem.put("tags", tagsSet);
                    }

                    mediaContainer.put("Media", mediaItem);
                    mediaResponse.put("data", mediaContainer);

                    return MAP_TO_MEDIADATA(mediaResponse);
                })
                .collect(Collectors.toList());
    }

    private List<MediaData> extractMediaListFromResponse(Map<String, Object> response) {
        try {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data == null) return List.of();

            Map<String, Object> page = (Map<String, Object>) data.get("Page");
            if (page == null) return List.of();

            List<Map<String, Object>> mediaList = (List<Map<String, Object>>) page.get("media");
            if (mediaList == null) return List.of();

            return mediaList.stream()
                    .map(mediaData -> {
                        Map<String, Object> mediaResponse = new HashMap<>();
                        Map<String, Object> mediaContainer = new HashMap<>();
                        mediaContainer.put("Media", mediaData);
                        mediaResponse.put("data", mediaContainer);

                        return MAP_TO_MEDIADATA(mediaResponse);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error extracting media list from response: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
