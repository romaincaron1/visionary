package com.romaincaron.data_collection.dto;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SyncResult {
    private boolean success;
    private String message;
}
