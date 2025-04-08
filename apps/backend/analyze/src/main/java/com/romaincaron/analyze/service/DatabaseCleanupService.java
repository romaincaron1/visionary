package com.romaincaron.analyze.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseCleanupService {

    private final Driver neo4jDriver;
    private static final Logger log = LoggerFactory.getLogger(DatabaseCleanupService.class);

    @PostConstruct
    public void cleanupDuplicates() {
        cleanupDuplicateGenres();
        cleanupDuplicateTags();
    }

    private void cleanupDuplicateTags() {
        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "MATCH (t:TagNode) " +
                            "WITH t.name as name, t.sourceName as sourceName, collect(t) as tags " +
                            "WHERE size(tags) > 1 " +
                            "WITH name, sourceName, tags[0] as original, tags[1..] as duplicates " +
                            "UNWIND duplicates as duplicate " +
                            "OPTIONAL MATCH (duplicate)<-[r:HAS_TAG]-(m:MediaNode) " +
                            "WITH name, sourceName, original, duplicate, m " +
                            "WHERE m IS NOT NULL " +
                            "MERGE (m)-[:HAS_TAG]->(original) " +
                            "WITH name, sourceName, original, duplicate " +
                            "DETACH DELETE duplicate " +
                            "RETURN count(*) as removedCount"
            );

            Record record = result.single();
            long removedCount = record.get("removedCount").asLong();

            if (removedCount > 0) {
                log.info("Cleaned up {} duplicate tag nodes", removedCount);
            }
        } catch (Exception e) {
            log.error("Error cleaning up duplicate tags: {}", e.getMessage(), e);
        }
    }

    private void cleanupDuplicateGenres() {
        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "MATCH (g:GenreNode) " +
                            "WITH g.name as name, collect(g) as genres " +
                            "WHERE size(genres) > 1 " +
                            "WITH name, genres[0] as original, genres[1..] as duplicates " +
                            "UNWIND duplicates as duplicate " +
                            "OPTIONAL MATCH (duplicate)<-[r:HAS_GENRE]-(m:MediaNode) " +
                            "WITH name, original, duplicate, m " +
                            "WHERE m IS NOT NULL " +
                            "MERGE (m)-[:HAS_GENRE]->(original) " +
                            "WITH name, original, duplicate " +
                            "DETACH DELETE duplicate " +
                            "RETURN count(*) as removedCount"
            );

            Record record = result.single();
            long removedCount = record.get("removedCount").asLong();

            if (removedCount > 0) {
                log.info("Cleaned up {} duplicate genre nodes", removedCount);
            }
        } catch (Exception e) {
            log.error("Error cleaning up duplicate genres: {}", e.getMessage(), e);
        }
    }

}
