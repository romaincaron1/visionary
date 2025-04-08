package com.romaincaron.analyze.service.entity.impl;

import com.romaincaron.analyze.entity.TagNode;
import com.romaincaron.analyze.repositories.TagNodeRepository;
import com.romaincaron.analyze.service.entity.TagNodeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TagNodeServiceImpl implements TagNodeService {

    private final TagNodeRepository tagNodeRepository;

    @Override
    public Optional<TagNode> findByNameAndSourceName(String name, String sourceName) {
        return tagNodeRepository.findByNameAndSourceName(name, sourceName);
    }

    @Override
    public List<TagNode> findMostPopularTags(int limit) {
        return tagNodeRepository.findMostPopularTags(limit);
    }

    @Override
    public List<TagNode> findAll() {
        return tagNodeRepository.findAll();
    }
}
