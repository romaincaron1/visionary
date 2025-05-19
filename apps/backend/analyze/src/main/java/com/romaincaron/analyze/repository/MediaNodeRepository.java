package com.romaincaron.analyze.repository;

import com.romaincaron.analyze.entity.MediaNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface MediaNodeRepository extends Neo4jRepository<MediaNode, Long> {
    Optional<MediaNode> findByExternalId(String externalId);
    Optional<MediaNode> findByExternalIdAndSourceName(String externalId, String sourceName);
    
    @Query("MATCH (m1:MediaNode {externalId: $externalId, sourceName: $sourceName})-[:HAS_GENRE]->(g:GenreNode)<-[:HAS_GENRE]-(m2:MediaNode) " +
           "WHERE m1 <> m2 " +
           "WITH m2, count(g) as commonGenres " +
           "RETURN m2 " +
           "ORDER BY commonGenres DESC " +
           "LIMIT $limit")
    List<MediaNode> findMediaWithSimilarGenres(String externalId, String sourceName, int limit);
    
    @Query("MATCH (m1:MediaNode {externalId: $externalId, sourceName: $sourceName})-[:HAS_TAG]->(t:TagNode)<-[:HAS_TAG]-(m2:MediaNode) " +
           "WHERE m1 <> m2 " +
           "WITH m2, count(t) as commonTags " +
           "RETURN m2 " +
           "ORDER BY commonTags DESC " +
           "LIMIT $limit")
    List<MediaNode> findMediaWithSimilarTags(String externalId, String sourceName, int limit);

    @Query("MATCH (m:MediaNode) WITH m.mediaType as type, COUNT(m) as count RETURN {type: type, count: count}")
    List<Map<String, Object>> countByMediaType();

    @Query("MATCH (m:MediaNode)-[r:HAS_TAG]->() WITH m, COUNT(r) as tagCount RETURN avg(tagCount)")
    double getAverageTagCount();

    @Query("MATCH (m:MediaNode)-[r:HAS_GENRE]->() WITH m, COUNT(r) as genreCount RETURN avg(genreCount)")
    double getAverageGenreCount();
} 