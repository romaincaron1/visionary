package com.romaincaron.data_collection.service.entity;

import com.romaincaron.data_collection.entity.MediaVector;
import jakarta.persistence.EntityNotFoundException;

public interface MediaVectorService {

    /**
     * Find a media vector by the media ID
     * @param id
     * @return MediaVector
     * @throws EntityNotFoundException if no media vector is found with the given ID
     */
    MediaVector findByMediaId(long id);

    /**
     * Save a media vector
     * @param mediaVector
     * @return
     */
    MediaVector save(MediaVector mediaVector);
}
