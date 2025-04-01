package com.romaincaron.data_collection.service.datasource;

import com.romaincaron.data_collection.dto.MediaData;
import com.romaincaron.data_collection.enums.MediaType;

import java.util.List;
import java.util.Optional;

public interface DataSource {
    /**
     * Get the unique name source
     * @return String
     */
    String getSourceName();

    /**
     * Get the media by its external ID
     * @param externalMediaId
     * @param mediaType
     * @return Optional<MediaData>
     */
    Optional<MediaData> fetchMediaById(String externalMediaId, MediaType mediaType);

    /**
     * Fetch the most recently updated medias since a defined date
     * @param mediaType
     * @param limit
     * @param page
     * @return List<MediaData>
     */
    List<MediaData> fetchRecentlyUpdatedMedia(MediaType mediaType, int limit, int page);

    /**
     * Fetch all medias
     * @param mediaType
     * @return List<MediaData>
     */
    List<MediaData> fetchAllMedia(MediaType mediaType);

    /**
     * Check if the data source is available
     * @return
     */
    boolean isAvailable();
}
