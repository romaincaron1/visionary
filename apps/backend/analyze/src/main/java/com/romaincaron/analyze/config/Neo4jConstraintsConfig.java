package com.romaincaron.analyze.config;

import jakarta.annotation.PostConstruct;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.slf4j.LoggerFactory.*;

@Configuration
public class Neo4jConstraintsConfig {

    private static final Logger log = getLogger(Neo4jConstraintsConfig.class);
    private final Driver neo4jDriver;

    public Neo4jConstraintsConfig(Driver neo4jDriver) {
        this.neo4jDriver = neo4jDriver;
    }

    @Bean
    public CommandLineRunner createGenreUniqueConstraint(Driver driver) {
        return args -> {
            try (Session session = driver.session()) {
                try {
                    session.run("CREATE CONSTRAINT unique_genre_name IF NOT EXISTS FOR (g:GenreNode) REQUIRE g.name IS UNIQUE");
                    log.info("Created unique constraint for GenreNode name property");

                    session.run("CREATE INDEX genre_name_index IF NOT EXISTS FOR (g:GenreNode) ON (g.name)");
                } catch (Exception e) {
                    log.error("Error creating Neo4j constraints: {}", e.getMessage(), e);
                }
            }
        };
    }

    @Bean
    public CommandLineRunner createTagUniqueConstraint(Driver driver) {
        return args -> {
            try (Session session = driver.session()) {
                try {
                    session.run("CREATE CONSTRAINT unique_genre_name IF NOT EXISTS FOR (g:GenreNode) REQUIRE g.name IS UNIQUE");

                    session.run("CREATE CONSTRAINT unique_tag_name_source IF NOT EXISTS FOR (t:TagNode) REQUIRE (t.name, t.sourceName) IS UNIQUE");

                    log.info("Created Neo4j constraints successfully");
                } catch (Exception e) {
                    log.error("Error creating Neo4j constraints: {}", e.getMessage(), e);
                }
            }
        };
    }

    @PostConstruct
    public void cleanupDuplicateTags() {
        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "MATCH (t:TagNode) " +
                            "WITH t.name as name, t.sourceName as sourceName, collect(t) as tags " +
                            "WHERE size(tags) > 1 " +
                            "WITH name, sourceName, tags[0] as original, tags[1..] as duplicates " +
                            "UNWIND duplicates as duplicate " +
                            "MATCH (duplicate)<-[r:HAS_TAG]-(m:MediaNode) " +
                            "MERGE (m)-[:HAS_TAG]->(original) " +
                            "WITH duplicate " +
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
}
