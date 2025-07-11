package com.romaincaron.data_collection.service.synchronization;

import com.romaincaron.data_collection.dto.MediaData;
import com.romaincaron.data_collection.entity.Media;
import com.romaincaron.data_collection.entity.MediaTag;
import com.romaincaron.data_collection.entity.Tag;
import com.romaincaron.data_collection.entity.embeddable.MediaTagId;
import com.romaincaron.data_collection.repository.MediaTagRepository;
import com.romaincaron.data_collection.repository.TagRepository;
import com.romaincaron.data_collection.util.ConfidenceLevelEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagSynchronizationService {
    private final TagRepository tagRepository;
    private final MediaTagRepository mediaTagRepository;

    public Tag findOrCreateTag(String name, String sourceName) {
        return tagRepository.findByNameAndSourceName(name, sourceName)
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(name);
                    newTag.setSourceName(sourceName);
                    return tagRepository.save(newTag);
                });
    }

    @Transactional
    public Set<MediaTag> createMediaTags(Set<MediaData.TagData> tagDataList, Media media) {
        if (media.getId() == null) {
            throw new IllegalArgumentException("Media must be persisted before creating MediaTags");
        }

        // Delete former tags
        mediaTagRepository.deleteAllByMediaId(media.getId());

        // Create new tags
        Set<MediaTag> mediaTags = new HashSet<>();

        if (tagDataList == null || tagDataList.isEmpty()) {
            return mediaTags;
        }

        for (MediaData.TagData tagData : tagDataList) {
            Tag tag = findOrCreateTag(tagData.getName(), media.getSourceName());

            if (tag.getId() == null) {
                tag = tagRepository.save(tag);
            }

            MediaTag mediaTag = new MediaTag();
            mediaTag.setMedia(media);
            mediaTag.setTag(tag);
            mediaTag.setRelevance(tagData.getRelevance());
            mediaTag.setConfidenceLevel(ConfidenceLevelEvaluator.EVALUATE(tagData.getRelevance()));

            MediaTagId mediaTagId = new MediaTagId();
            mediaTagId.setMediaId(media.getId());
            mediaTagId.setTagId(tag.getId());
            mediaTag.setId(mediaTagId);

            mediaTag = mediaTagRepository.save(mediaTag);

            mediaTags.add(mediaTag);
        }

        return mediaTags;
    }
}