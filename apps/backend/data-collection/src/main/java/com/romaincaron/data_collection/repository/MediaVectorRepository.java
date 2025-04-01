package com.romaincaron.data_collection.repository;

import com.romaincaron.data_collection.entity.MediaVector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaVectorRepository extends JpaRepository<MediaVector, Long> {
    Optional<MediaVector> findByMediaId(Long mediaId);
}
