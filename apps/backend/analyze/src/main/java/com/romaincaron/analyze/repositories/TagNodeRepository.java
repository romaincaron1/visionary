package com.romaincaron.analyze.repositories;

import com.romaincaron.analyze.entity.TagNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagNodeRepository extends Neo4jRepository<TagNode, Long> {

    // Find a tag by its name and source name
    Optional<TagNode> findByNameAndSourceName(String name, String sourceName);

    // Find most popular tags
    @Query("MATCH (t:TagNode)<-[r:HAS_TAG]-(m:MediaNode) " +
            "WITH t, COUNT(r) AS tagCount " +
            "RETURN t, tagCount " +
            "ORDER BY tagCount DESC LIMIT $limit")
    List<TagNode> findMostPopularTags(int limit);


}
