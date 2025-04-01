package com.romaincaron.analyze.entity;

import com.romaincaron.analyze.entity.relationships.TagRelationship;
import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

import java.util.HashSet;
import java.util.Set;

@Node("Tag")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
public class TagNode {
    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Property("sourceName")
    private String sourceName;

    @Relationship(type = "HAS_TAG", direction = Relationship.Direction.INCOMING)
    private Set<TagRelationship> mediaTags = new HashSet<>();
}