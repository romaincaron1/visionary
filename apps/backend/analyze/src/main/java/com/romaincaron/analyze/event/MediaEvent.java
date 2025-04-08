package com.romaincaron.analyze.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaEvent {
    private Long mediaId;
    private String eventType; // CREATED OR UPDATED
    private Long timestamp;
}
