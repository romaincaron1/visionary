package com.romaincaron.analyze.service.entity;

import com.romaincaron.analyze.entity.TagNode;

import java.util.List;
import java.util.Optional;

public interface TagNodeService {
    Optional<TagNode> findByNameAndSourceName(String name, String sourceName);
    List<TagNode> findMostPopularTags(int limit);
}
