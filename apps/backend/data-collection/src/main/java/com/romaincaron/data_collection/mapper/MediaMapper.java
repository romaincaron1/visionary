package com.romaincaron.data_collection.mapper;

import com.romaincaron.data_collection.dto.GenreDto;
import com.romaincaron.data_collection.dto.MediaDto;
import com.romaincaron.data_collection.dto.MediaTagDto;
import com.romaincaron.data_collection.dto.TagDto;
import com.romaincaron.data_collection.entity.Genre;
import com.romaincaron.data_collection.entity.Media;
import com.romaincaron.data_collection.entity.MediaTag;
import com.romaincaron.data_collection.entity.Tag;

import java.util.stream.Collectors;

public class MediaMapper {

    /**
     * Convert Media entity to MediaDto
     * @param media The Media entity to convert
     * @return A MediaDto object
     */
    public static MediaDto toDto(Media media) {
        if (media == null) {
            return null;
        }

        MediaDto dto = new MediaDto();
        dto.setId(media.getId());
        dto.setTitle(media.getTitle());
        dto.setExternalId(media.getExternalId());
        dto.setTitleAlternative(media.getTitleAlternative());
        dto.setMediaType(media.getMediaType());
        dto.setSynopsis(media.getSynopsis());
        dto.setAuthor(media.getAuthor());
        dto.setArtist(media.getArtist());
        dto.setStartYear(media.getStartYear());
        dto.setEndYear(media.getEndYear());
        dto.setStatus(media.getStatus());
        dto.setCoverUrl(media.getCoverUrl());
        dto.setRating(media.getRating());
        dto.setPopularity(media.getPopularity());
        dto.setSourceName(media.getSourceName());
        dto.setChecksum(media.getChecksum());
        dto.setLastUpdated(media.getLastUpdated());

        // Map genres
        if (media.getGenres() != null) {
            dto.setGenres(media.getGenres().stream()
                    .map(MediaMapper::toGenreDto)
                    .collect(Collectors.toSet()));
        }

        // Map media tags
        if (media.getMediaTags() != null) {
            dto.setMediaTags(media.getMediaTags().stream()
                    .map(MediaMapper::toMediaTagDto)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    /**
     * Convert MediaDto to Media entity
     * Note: This does not set relationships (genres, mediaTags) to avoid circular references
     * Those should be handled separately
     * @param dto The MediaDto to convert
     * @return A Media entity
     */
    public static Media toEntity(MediaDto dto) {
        if (dto == null) {
            return null;
        }

        Media media = new Media();
        updateEntityFromDto(media, dto);
        return media;
    }

    /**
     * Update a Media entity from a MediaDto
     * Note: This does not update relationships (genres, mediaTags) to avoid circular references
     * Those should be handled separately
     * @param media The Media entity to update
     * @param dto The MediaDto with the data
     * @return The updated Media entity
     */
    public static Media updateEntityFromDto(Media media, MediaDto dto) {
        if (dto == null) {
            return media;
        }

        // Only set ID if it's an update (not a new entity)
        if (dto.getId() != null) {
            media.setId(dto.getId());
        }

        media.setTitle(dto.getTitle());
        media.setExternalId(dto.getExternalId());
        media.setTitleAlternative(dto.getTitleAlternative());
        media.setMediaType(dto.getMediaType());
        media.setSynopsis(dto.getSynopsis());
        media.setAuthor(dto.getAuthor());
        media.setArtist(dto.getArtist());
        media.setStartYear(dto.getStartYear());
        media.setEndYear(dto.getEndYear());
        media.setStatus(dto.getStatus());
        media.setCoverUrl(dto.getCoverUrl());
        media.setRating(dto.getRating());
        media.setPopularity(dto.getPopularity());
        media.setSourceName(dto.getSourceName());
        media.setChecksum(dto.getChecksum());
        media.setLastUpdated(dto.getLastUpdated());

        return media;
    }

    /**
     * Convert Genre entity to GenreDto
     * @param genre The Genre entity to convert
     * @return A GenreDto object
     */
    public static GenreDto toGenreDto(Genre genre) {
        if (genre == null) {
            return null;
        }
        return new GenreDto(genre.getId(), genre.getName());
    }

    /**
     * Convert GenreDto to Genre entity
     * @param dto The GenreDto to convert
     * @return A Genre entity
     */
    public static Genre toGenreEntity(GenreDto dto) {
        if (dto == null) {
            return null;
        }
        Genre genre = new Genre();
        genre.setId(dto.getId());
        genre.setName(dto.getName());
        return genre;
    }

    /**
     * Convert Tag entity to TagDto
     * @param tag The Tag entity to convert
     * @return A TagDto object
     */
    public static TagDto toTagDto(Tag tag) {
        if (tag == null) {
            return null;
        }
        return new TagDto(tag.getId(), tag.getName(), tag.getSourceName());
    }

    /**
     * Convert MediaTag entity to MediaTagDto
     * @param mediaTag The MediaTag entity to convert
     * @return A MediaTagDto object
     */
    public static MediaTagDto toMediaTagDto(MediaTag mediaTag) {
        if (mediaTag == null) {
            return null;
        }

        TagDto tagDto = toTagDto(mediaTag.getTag());
        return new MediaTagDto(tagDto, mediaTag.getRelevance(), mediaTag.getConfidenceLevel() != null ? mediaTag.getConfidenceLevel().toString() : null);
    }
}