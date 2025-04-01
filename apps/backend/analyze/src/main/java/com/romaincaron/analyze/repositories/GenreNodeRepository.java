package com.romaincaron.analyze.repositories;

import com.romaincaron.analyze.entity.GenreNode;
import com.romaincaron.analyze.entity.MediaNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreNodeRepository extends Neo4jRepository<GenreNode, Long> {

    // Find a genre by its name
    Optional<GenreNode> findByName(String name);

    // Find medias with a specific genre
    @Query("MATCH (g:Genre)<-[:HAS_GENRE]-(m:Media) WHERE g.name = $genreName RETURN m")
    List<MediaNode> findMediaByGenre(String genreName);
}
