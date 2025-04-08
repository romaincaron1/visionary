package com.romaincaron.analyze.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.romaincaron.analyze.entity.relationships.SimilarityRelationship;
import com.romaincaron.analyze.entity.relationships.TagRelationship;
import com.romaincaron.analyze.enums.MediaType;
import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

import java.util.HashSet;
import java.util.Set;

@Node("MediaNode")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
public class MediaNode {

    @Id @GeneratedValue
    private Long id;

    @Property("externalId")
    private String externalId;

    @Property("sourceName")
    private String sourceName;

    @Property("title")
    private String title;

    @Property("synopsis")
    private String synopsis;

    @Property("mediaType")
    private MediaType mediaType;

    // Relations
    @Relationship(type = "HAS_GENRE")
    private Set<GenreNode> genres = new HashSet<>();

    @Relationship(type = "HAS_TAG")
    private Set<TagRelationship> tags = new HashSet<>();

    @Relationship(type = "SIMILAR_TO")
    private Set<SimilarityRelationship> similarMedia = new HashSet<>();

    @Property("contentVector")
    private String contentVector;

    // Utils Methods
    public void setVectorFromDoubleArray(double[] vector) {
        try {
            this.contentVector = new ObjectMapper().writeValueAsString(vector);
        } catch(JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize vector", e);
        }
    }

    public double[] getVectorAsDoubleArray() {
        if (contentVector == null) {
            return new double[0];
        }

        try {
            return new ObjectMapper().readValue(contentVector, double[].class);
        } catch(JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize vector", e);
        }
    }
}
