package com.romaincaron.analyze.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

import java.util.HashSet;
import java.util.Set;

@Node("GenreNode")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
public class GenreNode {
    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Relationship(type = "HAS_GENRE", direction = Relationship.Direction.INCOMING)
    private Set<MediaNode> media = new HashSet<>();
}
