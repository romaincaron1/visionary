package com.romaincaron.analyze.entity.relationships;

import com.romaincaron.analyze.entity.TagNode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

@RelationshipProperties
@Getter @Setter
public class TagRelationship {
    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private TagNode tag;

    @Property("relevance")
    private Integer relevance;

    @Property("confidenceLevel")
    private String confidenceLevel;
}