package com.romaincaron.analyze.service.synchronization.impl;

import com.romaincaron.analyze.dto.MediaDto;
import com.romaincaron.analyze.dto.MediaTagDto;
import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.entity.TagNode;
import com.romaincaron.analyze.entity.relationships.TagRelationship;
import com.romaincaron.analyze.repositories.TagNodeRepository;
import com.romaincaron.analyze.service.entity.TagNodeService;
import com.romaincaron.analyze.service.synchronization.TagSynchronizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultTagSynchronizer implements TagSynchronizer {

    private final TagNodeService tagNodeService;
    private final TagNodeRepository tagNodeRepository;

    @Override
    public void synchronize(MediaNode mediaNode, MediaDto mediaDto) {
        Set<TagRelationship> newRelationships = new HashSet<>();

        if (mediaDto.getMediaTags() != null && !mediaDto.getMediaTags().isEmpty()) {
            for (MediaTagDto mediaTagDto : mediaDto.getMediaTags()) {
                TagNode tagNode = findOrCreateTag(mediaTagDto.getTag().getName(), mediaDto.getSourceName());

                Optional<TagRelationship> existingRelOpt = mediaNode.getTags().stream()
                        .filter(rel -> rel.getTag().getId() != null
                                && tagNode.getId() != null
                                && rel.getTag().getId().equals(tagNode.getId()))
                        .findFirst();

                if (existingRelOpt.isPresent()) {
                    TagRelationship rel = existingRelOpt.get();
                    boolean changed = updateTagRelationship(rel, mediaTagDto, mediaDto.getSourceName());

                    if (changed) {
                        log.info("Updated tag relationship '{}' for '{}'",
                                tagNode.getName(), mediaNode.getTitle());
                    }

                    newRelationships.add(rel);
                } else {
                    TagRelationship newRel = createTagRelationship(tagNode, mediaTagDto);
                    newRelationships.add(newRel);
                    log.info("Created new tag relationship '{}' for '{}' with relevance {} and confidenceLevel {}",
                            tagNode.getName(), mediaNode.getTitle(), mediaTagDto.getRelevance(), mediaTagDto.getConfidenceLevel());
                }
            }
        }

        int removedCount = mediaNode.getTags().size() - newRelationships.size();
        if (removedCount > 0) {
            log.info("Removing {} obsolete tag relationships for {}", removedCount, mediaNode.getTitle());
        }

        mediaNode.getTags().clear();
        mediaNode.getTags().addAll(newRelationships);
    }

    @Transactional
    protected TagNode findOrCreateTag(String tagName, String sourceName) {
        Optional<TagNode> existingTag = tagNodeService.findByNameAndSourceName(tagName, sourceName);

        if (existingTag.isPresent()) {
            return existingTag.get();
        } else {
            TagNode newTag = new TagNode();
            newTag.setName(tagName);
            newTag.setSourceName(sourceName);
            TagNode savedTag = tagNodeRepository.save(newTag);
            log.info("Created new tag: {} (source: {})", tagName, sourceName);
            return savedTag;
        }
    }

    private boolean updateTagRelationship(
            TagRelationship relationship, MediaTagDto mediaTagDto, String sourceName) {
        boolean changed = false;

        if (mediaTagDto.getRelevance() != null
                && !mediaTagDto.getRelevance().equals(relationship.getRelevance())) {
            relationship.setRelevance(mediaTagDto.getRelevance());
            changed = true;
        }

        if (mediaTagDto.getConfidenceLevel() != null
                && !mediaTagDto.getConfidenceLevel().equals(relationship.getConfidenceLevel())) {
            relationship.setConfidenceLevel(mediaTagDto.getConfidenceLevel());
            changed = true;
        }

        return changed;
    }

    private TagRelationship createTagRelationship(
            TagNode tagNode, MediaTagDto mediaTagDto) {
        TagRelationship relationship = new TagRelationship();
        relationship.setTag(tagNode);
        relationship.setRelevance(mediaTagDto.getRelevance());
        relationship.setConfidenceLevel(mediaTagDto.getConfidenceLevel());
        return relationship;
    }
}