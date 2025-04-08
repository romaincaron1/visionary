package com.romaincaron.analyze.service.synchronization.impl;

import com.romaincaron.analyze.dto.MediaDto;
import com.romaincaron.analyze.dto.MediaTagDto;
import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.entity.TagNode;
import com.romaincaron.analyze.entity.relationships.TagRelationship;
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

    @Override
    public void synchronize(MediaNode mediaNode, MediaDto mediaDto) {
        if (mediaDto.getMediaTags() == null || mediaDto.getMediaTags().isEmpty()) {
            mediaNode.getTags().clear();
            return;
        }

        Set<TagRelationship> currentRelationships = new HashSet<>(mediaNode.getTags());
        Set<TagRelationship> updatedRelationships = new HashSet<>();

        for (MediaTagDto mediaTagDto : mediaDto.getMediaTags()) {
            TagNode tagNode = findOrCreateTag(mediaTagDto.getTag().getName(), mediaDto.getSourceName());

            // Find if there's an existing relationship
            Optional<TagRelationship> existingRelOpt = findExistingTagRelationship(currentRelationships, tagNode);

            if (existingRelOpt.isPresent()) {
                // Update existing relationship
                TagRelationship rel = existingRelOpt.get();
                boolean changed = updateTagRelationship(rel, mediaTagDto, mediaDto.getSourceName());

                if (changed) {
                    log.info("Updated tag relationship '{}' for '{}'",
                            tagNode.getName(), mediaNode.getTitle());
                }

                updatedRelationships.add(rel);
            } else {
                // Create new relationship
                TagRelationship newRel = createTagRelationship(tagNode, mediaTagDto);
                updatedRelationships.add(newRel);
                log.info("Created new tag relationship '{}' for '{}' with relevance {} and confidenceLevel {}",
                        tagNode.getName(), mediaNode.getTitle(), mediaTagDto.getRelevance(), mediaTagDto.getConfidenceLevel());
            }
        }

        // Remove obsolete relationships
        Set<TagRelationship> toRemove = new HashSet<>(currentRelationships);
        toRemove.removeAll(updatedRelationships);
        if (!toRemove.isEmpty()) {
            mediaNode.getTags().removeAll(toRemove);
            log.info("Removed {} obsolete tag relationships for {}",
                    toRemove.size(), mediaNode.getTitle());
        }

        // Add new relationships
        Set<TagRelationship> toAdd = new HashSet<>(updatedRelationships);
        toAdd.removeAll(currentRelationships);
        for (TagRelationship rel : toAdd) {
            mediaNode.getTags().add(rel);
        }
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
            log.info("Created new tag: {} (source: {})", tagName, sourceName);
            return newTag;
        }
    }

    private Optional<TagRelationship> findExistingTagRelationship(
            Set<TagRelationship> relationships, TagNode tagNode) {
        return relationships.stream()
                .filter(rel -> rel.getTag().getId() != null
                        && rel.getTag().getId().equals(tagNode.getId()))
                .findFirst();
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
