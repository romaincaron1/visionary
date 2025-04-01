package com.romaincaron.data_collection.repository;

import com.romaincaron.data_collection.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByNameAndSourceName(String name, String sourceName);
}
