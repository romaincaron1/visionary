package com.romaincaron.analyze.service.synchronization.impl;

import com.romaincaron.analyze.dto.MediaDto;
import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.entity.TagNode;
import com.romaincaron.analyze.entity.relationships.TagRelationship;
import com.romaincaron.analyze.service.entity.TagNodeService;
import com.romaincaron.analyze.service.synchronization.ConfidenceLevelEvaluator;
import com.romaincaron.analyze.service.synchronization.TagSynchronizer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DefaultTagSynchronizer implements TagSynchronizer {
    private static final Logger log = LoggerFactory.getLogger(DefaultTagSynchronizer.class);

    private final TagNodeService tagNodeService;
    private final ConfidenceLevelEvaluator confidenceLevelEvaluator;

    @Override
    public void synchronize(MediaNode mediaNode, MediaDto mediaDto) {
        if (mediaDto.getTags() == null || mediaDto.getTags().isEmpty()) {
            mediaNode.getTags().clear();
            return;
        }

        Set<TagRelationship> currentRelationships = new HashSet<>(mediaNode.getTags());
        Set<TagRelationship> updatedRelationships = new HashSet<>();

        for (MediaDto.TagDto tagDto : mediaDto.getTags()) {
            TagNode tagNode = findOrCreateTag(tagDto.getName(), mediaDto.getSourceName());

            // Find if there's an existing relationship
            Optional<TagRelationship> existingRelOpt = findExistingTagRelationship(currentRelationships, tagNode);

            if (existingRelOpt.isPresent()) {
                // Update existing relationship
                TagRelationship rel = existingRelOpt.get();
                boolean changed = updateTagRelationship(rel, tagDto, mediaDto.getSourceName());

                if (changed) {
                    log.info("Updated tag relationship '{}' for '{}'",
                            tagNode.getName(), mediaNode.getTitle());
                }

                updatedRelationships.add(rel);
            } else {
                // Create new relationship
                TagRelationship newRel = createTagRelationship(tagNode, tagDto, mediaDto.getSourceName());
                updatedRelationships.add(newRel);
                log.info("Created new tag relationship '{}' for '{}' with relevance {}",
                        tagNode.getName(), mediaNode.getTitle(), tagDto.getRelevance());
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

    private TagNode findOrCreateTag(String tagName, String sourceName) {
        Optional<TagNode> existingTag = tagNodeService.findByNameAndSourceName(tagName, sourceName);

        if (existingTag.isPresent()) {
            return existingTag.get();
        } else {
            TagNode newTag = new TagNode();
            newTag.setName(tagName);
            newTag.setSourceName(sourceName);
            // Assuming you have a save method in the service
            // If not, you'll need to add one
            // TagNode savedTag = tagNodeService.save(newTag);
            log.info("Created new tag: {} (source: {})", tagName, sourceName);
            return newTag; // or savedTag if you have the save method
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
            TagRelationship relationship, MediaDto.TagDto tagDto, String sourceName) {
        boolean changed = false;

        if (tagDto.getRelevance() != null
                && !tagDto.getRelevance().equals(relationship.getRelevance())) {
            relationship.setRelevance(tagDto.getRelevance());
            changed = true;
        }

        String confidenceLevel = confidenceLevelEvaluator.evaluateConfidenceLevel(
                sourceName, tagDto.getRelevance());
        if (!confidenceLevel.equals(relationship.getConfidenceLevel())) {
            relationship.setConfidenceLevel(confidenceLevel);
            changed = true;
        }

        return changed;
    }

    private TagRelationship createTagRelationship(
            TagNode tagNode, MediaDto.TagDto tagDto, String sourceName) {
        TagRelationship relationship = new TagRelationship();
        relationship.setTag(tagNode);
        relationship.setRelevance(tagDto.getRelevance());
        relationship.setConfidenceLevel(
                confidenceLevelEvaluator.evaluateConfidenceLevel(sourceName, tagDto.getRelevance()));
        return relationship;
    }
}
