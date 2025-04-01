package com.romaincaron.analyze.repositories;

import com.romaincaron.analyze.entity.MediaNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaNodeRepository extends Neo4jRepository<MediaNode, Long> {

    // Find a media by its ID and source name
    Optional<MediaNode> findByExternalIdAndSourceName(String externalId, String sourceName);

    // Find similar medias
    @Query("MATCH (m:Media)-[r:SIMILAR_TO]->(similar:Media) " +
            "WHERE m.externalId = $externalId AND m.sourceName = $sourceName " +
            "RETURN similar ORDER BY r.score DESC LIMIT $limit")
    List<MediaNode> findSimilarMedia(String externalId, String sourceName, int limit);

    // Find medias with similar genres
    @Query("MATCH (m:Media)-[:HAS_GENRE]->(g:Genre)<-[:HAS_GENRE]-(similar:Media) " +
            "WHERE m.externalId = $externalId AND m.sourceName = $sourceName " +
            "AND similar <> m " +
            "WITH similar, COUNT(g) AS commonGenres " +
            "RETURN similar, commonGenres " +
            "ORDER BY commonGenres DESC LIMIT $limit")
    List<MediaNode> findMediaWithSimilarGenres(String externalId, String sourceName, int limit);

    // Find medias with similar tags
    @Query("MATCH (m:Media)-[r1:HAS_TAG]->(t:Tag)<-[r2:HAS_TAG]-(similar:Media) " +
            "WHERE m.externalId = $externalId AND m.sourceName = $sourceName " +
            "AND similar <> m " +
            "WITH similar, SUM(r1.relevance * r2.relevance) AS tagRelevance " +
            "RETURN similar, tagRelevance " +
            "ORDER BY tagRelevance DESC LIMIT $limit")
    List<MediaNode> findMediaWithSimilarTags(String externalId, String sourceName, int limit);
}
