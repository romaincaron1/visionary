package com.romaincaron.analyze.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Neo4jConnectionTest {

    private final static Logger logger = LoggerFactory.getLogger(Neo4jConnectionTest.class);

    @Value("${spring.neo4j.uri}")
    private String neo4jUri;

    @Value("${spring.neo4j.authentication.username}")
    private String neo4jUsername;

    @Value("${spring.neo4j.authentication.password}")
    private String neo4jPassword;

    @Bean
    public CommandLineRunner testNeo4jConnection() {
        return args -> {
            logger.info("Testing connection to Neo4j...");
            logger.info("URI: {}", neo4jUri);
            logger.info("Username: {}", neo4jUsername);

            try (Driver driver = GraphDatabase.driver(
                    neo4jUri,
                    AuthTokens.basic(neo4jUsername, neo4jPassword))) {

                driver.verifyConnectivity();
                logger.info("Neo4j connection successful!");

                try (Session session = driver.session()) {
                    String result = session.run("RETURN 'Neo4j connection is working!' AS message")
                            .single()
                            .get("message")
                            .asString();
                    logger.info("Query result: {}", result);
                }
            } catch (Exception e) {
                logger.error("Failed to connect to Neo4j: {}", e.getMessage(), e);
                throw e;
            }
        };
    }

}
