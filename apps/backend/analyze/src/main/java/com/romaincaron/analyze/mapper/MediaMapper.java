package com.romaincaron.analyze.mapper;

import com.romaincaron.analyze.dto.GenreDto;
import com.romaincaron.analyze.dto.MediaDto;
import com.romaincaron.analyze.dto.MediaTagDto;
import com.romaincaron.analyze.dto.TagDto;
import com.romaincaron.analyze.entity.GenreNode;
import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.entity.TagNode;
import com.romaincaron.analyze.entity.relationships.TagRelationship;

public class MediaMapper {

    /**
     * Map a MediaDto to a MediaNode
     * @param mediaNode
     * @param mediaDto
     * @return MediaNode
     */
    public static MediaNode MAP_TO_MEDIA_NODE(MediaNode mediaNode, MediaDto mediaDto) {
        mediaNode.setExternalId(mediaDto.getExternalId());
        mediaNode.setTitle(mediaDto.getTitle());
        mediaNode.setSourceName(mediaDto.getSourceName());
        mediaNode.setMediaType(mediaDto.getMediaType());
        mediaNode.setSynopsis(mediaDto.getSynopsis());
        return mediaNode;
    }

    /**
     *
     * @param mediaNode
     * @return
     */
    public static MediaDto MAP_TO_MEDIA_DTO(MediaNode mediaNode) {
        MediaDto mediaDto = new MediaDto();
        mediaDto.setId(mediaNode.getId());
        mediaDto.setChecksum(mediaNode.getChecksum());
        mediaDto.setExternalId(mediaNode.getExternalId());
        mediaDto.setTitle(mediaNode.getTitle());
        mediaDto.setSourceName(mediaNode.getSourceName());
        mediaDto.setMediaType(mediaNode.getMediaType());
        mediaDto.setSynopsis(mediaNode.getSynopsis());

        // Handle genres
        for (GenreNode genreNode : mediaNode.getGenres()) {
            mediaDto.getGenres().add(mapGenreToGenreDto(genreNode, new GenreDto()));
        }

        // Handle tags
        for (TagRelationship tagRelationship : mediaNode.getTags()) {
            mediaDto.getMediaTags().add(mapTagToMediaTagDto(tagRelationship));
        }

        return mediaDto;
    }

    private static GenreDto mapGenreToGenreDto(GenreNode genre, GenreDto genreDto) {
        genreDto.setId(genre.getId());
        genreDto.setName(genre.getName());
        return genreDto;
    }

    private static MediaTagDto mapTagToMediaTagDto(TagRelationship tagRelationship) {
        MediaTagDto mediaTagDto = new MediaTagDto();
        mediaTagDto.setTag(mapTagToTagDto(tagRelationship.getTag(), new TagDto()));
        mediaTagDto.setRelevance(tagRelationship.getRelevance());
        mediaTagDto.setConfidenceLevel(tagRelationship.getConfidenceLevel());
        return mediaTagDto;
    }

    private static TagDto mapTagToTagDto(TagNode TagNode, TagDto tagDto) {
        tagDto.setId(TagNode.getId());
        tagDto.setName(TagNode.getName());
        return tagDto;
    }
}
