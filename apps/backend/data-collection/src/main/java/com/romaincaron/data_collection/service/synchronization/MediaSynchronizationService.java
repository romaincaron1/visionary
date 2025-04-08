package com.romaincaron.data_collection.service.synchronization;

import com.romaincaron.data_collection.dto.MediaData;
import com.romaincaron.data_collection.dto.MediaDto;
import com.romaincaron.data_collection.dto.SyncResult;
import com.romaincaron.data_collection.entity.Media;
import com.romaincaron.data_collection.enums.MediaType;
import com.romaincaron.data_collection.event.MediaEventPublisher;
import com.romaincaron.data_collection.mapper.MediaMapper;
import com.romaincaron.data_collection.service.datasource.DataSource;
import com.romaincaron.data_collection.service.datasource.DataSourceManager;
import com.romaincaron.data_collection.service.entity.MediaService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class MediaSynchronizationService {

    private final DataSourceManager dataSourceManager;
    private final MediaService mediaService;
    private final GenreSynchronizationService genreService;
    private final TagSynchronizationService tagService;
    private final MediaEventPublisher eventPublisher;

    /**
     * Synchronizes all media of a specific type from all available data sources
     * @param mediaType The type of media to synchronize
     * @return A summary of the synchronization results
     */
    public SyncResult syncAllMedia(MediaType mediaType) {
        int totalCreated = 0;
        int totalUpdated = 0;
        int totalErrors = 0;

        for (DataSource dataSource : dataSourceManager.getAvailableSources()) {
            if (!dataSource.isAvailable()) continue;

            try {
                log.info("Fetching all media of type {} from {}", mediaType, dataSource.getSourceName());
                List<MediaData> mediaDatas = dataSource.fetchAllMedia(mediaType);
                log.info("Fetched {} media items from {}", mediaDatas.size(), dataSource.getSourceName());

                for (MediaData mediaData : mediaDatas) {
                    try {
                        boolean isNew = syncSingleMediaData(mediaData);
                        if (isNew) totalCreated++; else totalUpdated++;
                    } catch(Exception e) {
                        log.error("Error syncing media {}: {}", mediaData.getExternalId(), e.getMessage(), e);
                        totalErrors++;
                    }
                }
            } catch (Exception e) {
                log.error("Error fetching media from source {}: {}", dataSource.getSourceName(), e.getMessage(), e);
                totalErrors++;
            }
        }

        return new SyncResult()
                .setSuccess(totalErrors == 0)
                .setMessage(String.format("Processed %d media: %d created, %d updated, %d errors",
                        totalCreated + totalUpdated + totalErrors, totalCreated, totalUpdated, totalErrors));
    }

    /**
     * Sync a single media independently
     * @param mediaData
     * @return boolean
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean syncSingleMediaData(MediaData mediaData) {
        return syncMediaData(mediaData);
    }

    /**
     * Create or update a media if it already exists
     * @param mediaData
     * @return boolean
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public boolean syncMediaData(MediaData mediaData) {
        boolean isNew = false;
        Optional<MediaDto> existingMediaDtoOpt = mediaService.findByExternalIdAndSourceName(
                mediaData.getExternalId(), mediaData.getSourceName());

        Media media;
        if (existingMediaDtoOpt.isPresent()) {
            MediaDto mediaDto = existingMediaDtoOpt.get();
            media = mediaService.getEntityById(mediaDto.getId());
            syncMediaProperties(media, mediaData);
        } else {
            media = new Media();
            media.setExternalId(mediaData.getExternalId());
            media.setSourceName(mediaData.getSourceName());
            media = syncMediaProperties(media, mediaData);
            isNew = true;
        }

        if (isNew) {
            eventPublisher.publishMediaCreated(media.getId());
        } else {
            eventPublisher.publishMediaUpdated(media.getId());
        }

        return isNew;
    }

    /**
     * Sync in base the updated or created media
     * @param media
     * @param mediaData
     * @return Media
     */
    private Media syncMediaProperties(Media media, MediaData mediaData) {
        updateBasicProperties(media, mediaData);

        // First save the media to get an ID
        if (media.getId() == null) {
            try {
                MediaDto mediaDto = MediaMapper.toDto(media);
                mediaDto = mediaService.save(mediaDto);
                media = mediaService.getEntityById(mediaDto.getId());
            } catch (Exception e) {
                log.error("Failed to save media entity: {}", e.getMessage());
                throw e; // Re-throw to trigger transaction rollback for this specific media
            }
        }

        // After media is saved successfully, proceed with relationships
        try {
            // Handle genres
            media.getGenres().clear();
            media.getGenres().addAll(genreService.syncGenres(mediaData.getGenres() != null ?
                    mediaData.getGenres() : Set.of()));

            MediaDto mediaDto = MediaMapper.toDto(media);
            mediaDto = mediaService.save(mediaDto);
            media = mediaService.getEntityById(mediaDto.getId());

            // Handle tags
            media.getMediaTags().clear();
            if (mediaData.getTags() != null) {
                media.getMediaTags().addAll(tagService.createMediaTags(mediaData.getTags(), media));
            }

            mediaDto = MediaMapper.toDto(media);
            mediaDto = mediaService.save(mediaDto);
            return mediaService.getEntityById(mediaDto.getId());
        } catch (Exception e) {
            log.error("Failed to save media relationships: {}", e.getMessage());
            throw e; // Re-throw to trigger transaction rollback for this specific media
        }
    }

    private void updateBasicProperties(Media media, MediaData data) {
        media.setTitle(data.getTitle());
        media.setTitleAlternative(data.getTitleAlternative());
        media.setSynopsis(data.getSynopsis());
        media.setMediaType(data.getMediaType());
        media.setAuthor(data.getAuthor());
        media.setArtist(data.getArtist());
        media.setStartYear(data.getStartYear());
        media.setEndYear(data.getEndYear());
        media.setStatus(data.getStatus());
        media.setCoverUrl(data.getCoverUrl());
        media.setRating(data.getRating());
        media.setPopularity(data.getPopularity());
        media.setLastUpdated(LocalDateTime.now());
    }
}