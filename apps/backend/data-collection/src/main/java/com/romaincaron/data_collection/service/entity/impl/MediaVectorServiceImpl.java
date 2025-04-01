package com.romaincaron.data_collection.service.entity.impl;

import com.romaincaron.data_collection.entity.MediaVector;
import com.romaincaron.data_collection.repository.MediaVectorRepository;
import com.romaincaron.data_collection.service.entity.MediaVectorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MediaVectorServiceImpl implements MediaVectorService {

    private final MediaVectorRepository mediaVectorRepository;

    @Override
    public MediaVector findByMediaId(long id) {
        return mediaVectorRepository.findByMediaId(id)
                .orElseThrow(() -> new EntityNotFoundException("Media Vector not found with id: " + id));
    }

    @Override
    public MediaVector save(MediaVector mediaVector) {
        return mediaVectorRepository.save(mediaVector);
    }

}
