package com.romaincaron.data_collection.repository;

import com.romaincaron.data_collection.entity.MediaTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaTagRepository extends JpaRepository<MediaTag, Long> {
}
