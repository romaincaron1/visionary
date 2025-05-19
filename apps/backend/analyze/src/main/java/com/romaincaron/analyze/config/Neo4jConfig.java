package com.romaincaron.analyze.config;

import org.neo4j.driver.Driver;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.Neo4jClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class Neo4jConfig {

    @Autowired
    private Neo4jClient neo4jClient;

    @PostConstruct
    public void createIndexes() {
        // Index composite sur MediaNode
        neo4jClient.query("""
            CREATE INDEX media_composite IF NOT EXISTS
            FOR (m:MediaNode)
            ON (m.mediaType, m.externalId)
            """).run();

        // Index sur les relations HAS_TAG
        neo4jClient.query("""
            CREATE INDEX media_tag_rel IF NOT EXISTS
            FOR ()-[r:HAS_TAG]->()
            ON r.type
            """).run();

        // Index sur les relations HAS_GENRE
        neo4jClient.query("""
            CREATE INDEX media_genre_rel IF NOT EXISTS
            FOR ()-[r:HAS_GENRE]->()
            ON r.type
            """).run();
    }
} 