package com.romaincaron.analyze.service.synchronization;

import com.romaincaron.analyze.dto.MediaDto;
import com.romaincaron.analyze.entity.MediaNode;

public interface MediaSynchronizer {
    MediaNode synchronize(MediaDto mediaDto);
}
