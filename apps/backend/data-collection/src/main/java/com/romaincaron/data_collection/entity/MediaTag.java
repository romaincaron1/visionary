package com.romaincaron.data_collection.entity;

import com.romaincaron.data_collection.entity.embeddable.MediaTagId;
import com.romaincaron.data_collection.enums.ConfidenceLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "media_tags")
@Getter
@Setter
public class MediaTag {
    @EmbeddedId
    private MediaTagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("mediaId")
    private Media media;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    private Tag tag;

    @Column(name = "relevance")
    private Integer relevance;

    @Column(name = "confidence_level")
    @Enumerated(EnumType.STRING)
    private ConfidenceLevel confidenceLevel; // LOW, MEDIUM, HIGH
}

