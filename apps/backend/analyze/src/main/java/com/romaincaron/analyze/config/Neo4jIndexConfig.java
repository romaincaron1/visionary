package com.romaincaron.analyze.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class Neo4jIndexConfig {

    private final Driver neo4jDriver;

    @EventListener(ContextRefreshedEvent.class)
    public void createIndexes() {
        try (Session session = neo4jDriver.session()) {
            // Index sur externalId
            session.run("""
                CREATE INDEX media_external_id IF NOT EXISTS
                FOR (m:MediaNode)
                ON (m.externalId)
            """);

            // Index sur mediaType
            session.run("""
                CREATE INDEX media_type IF NOT EXISTS
                FOR (m:MediaNode)
                ON (m.mediaType)
            """);

            // Index composite sur externalId et mediaType
            session.run("""
                CREATE INDEX media_external_id_type IF NOT EXISTS
                FOR (m:MediaNode)
                ON (m.externalId, m.mediaType)
            """);

            // Index sur les relations HAS_TAG
            session.run("""
                CREATE INDEX media_tags IF NOT EXISTS
                FOR ()-[r:HAS_TAG]-()
                ON (r)
            """);

            // Index sur les relations HAS_GENRE
            session.run("""
                CREATE INDEX media_genres IF NOT EXISTS
                FOR ()-[r:HAS_GENRE]-()
                ON (r)
            """);

            if (log.isInfoEnabled()) {
                log.info("Neo4j indexes created successfully");
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Error creating Neo4j indexes: {}", e.getMessage());
            }
        }
    }
} 