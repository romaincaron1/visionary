package com.romaincaron.analyze.mapper;

import com.romaincaron.analyze.dto.MediaDto;
import com.romaincaron.analyze.entity.MediaNode;

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
}
