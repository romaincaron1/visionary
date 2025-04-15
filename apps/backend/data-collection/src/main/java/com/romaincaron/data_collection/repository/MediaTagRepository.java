package com.romaincaron.data_collection.repository;

import com.romaincaron.data_collection.entity.MediaTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaTagRepository extends JpaRepository<MediaTag, Long> {
    @Modifying
    @Query("DELETE FROM MediaTag mt WHERE mt.media.id = :mediaId")
    void deleteAllByMediaId(Long mediaId);
}
