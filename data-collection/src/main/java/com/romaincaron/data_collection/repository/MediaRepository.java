package com.romaincaron.data_collection.repository;

import com.romaincaron.data_collection.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    Optional<Media> findByExternalIdAndSourceName(String externalId, String sourceName);
}
