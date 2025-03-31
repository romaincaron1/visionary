package com.romaincaron.data_collection.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "media_vectors")
@Getter
@Setter
public class MediaVector {
    @Id
    @Column(name = "media_id")
    private Long mediaId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "media_id")
    private Media media;

    @Column(name = "tag_vector",columnDefinition = "jsonb")
    private String tagVector;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
