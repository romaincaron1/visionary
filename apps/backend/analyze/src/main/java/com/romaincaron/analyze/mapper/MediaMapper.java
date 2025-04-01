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
        return MediaNode.builder()
                .externalId(mediaDto.getExternalId())
                .title(mediaDto.getTitle())
                .sourceName(mediaDto.getSourceName())
                .mediaType(mediaDto.getMediaType().toString())
                .synopsis(mediaDto.getSynopsis())
                .build();
    }

}
