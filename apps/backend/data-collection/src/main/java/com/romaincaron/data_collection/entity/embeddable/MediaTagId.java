package com.romaincaron.data_collection.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class MediaTagId implements Serializable {
    @Column(name = "media_id")
    private Long mediaId;
    
    @Column(name = "tag_id")
    private Long tagId;
}
