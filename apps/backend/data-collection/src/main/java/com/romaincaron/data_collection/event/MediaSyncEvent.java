package com.romaincaron.data_collection.event;

import com.romaincaron.data_collection.enums.EventType;
import lombok.*;

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
