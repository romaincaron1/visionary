package com.romaincaron.analyze.event;

import com.romaincaron.analyze.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaSyncEvent {
    private EventType type; // MEDIA_SYNCED, BATCH_COMPLETED or SYNC_COMPLETED
    private String payload;
    private String sourceName;
    private LocalDateTime timestamp;
    private boolean singleUpdate = false;
}
