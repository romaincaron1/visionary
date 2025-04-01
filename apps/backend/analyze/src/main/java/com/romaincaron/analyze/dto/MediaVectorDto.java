package com.romaincaron.analyze.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MediaVectorDto {
    private Long mediaId;
    private String tagVector;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
