package com.romaincaron.analyze.entity.relationships;

import com.romaincaron.analyze.entity.MediaNode;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;

@RelationshipProperties
public class SimilarityRelationship {
    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private MediaNode target;

    @Property("score")
    private Double score;

    @Property("vectorSimilarity")
    private Double vectorSimilarity;

    @Property("genreSimilarity")
    private Double genreSimilarity;

    @Property("tagSimilarity")
    private Double tagSimilarity;

    @Property("reasons")
    private String reasons;

    @Property("lastCalculated")
    private LocalDateTime lastCalculated;
}