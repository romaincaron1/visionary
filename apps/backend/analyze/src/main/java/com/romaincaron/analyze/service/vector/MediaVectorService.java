package com.romaincaron.analyze.service.vector;

import com.romaincaron.analyze.entity.GenreNode;
import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.entity.relationships.TagRelationship;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MediaVectorService {

    private final TagDictionaryService tagDictionaryService;
    private final GenreDictionaryService genreDictionaryService;

    /**
     * Generate a vector for a MediaNode with its tags and genres
     * @param mediaNode
     * @return
     */
    public double[] generateMediaVector(MediaNode mediaNode) {
        // Get tags and genres dictionaries
        Map<String, Integer> tagIndexMap = tagDictionaryService.getTagIndexMap();
        Map<String, Integer> genreIndexMap = genreDictionaryService.getGenreIndexMap();

        // Calculate dimensions
        int tagDimension = tagIndexMap.size();
        int genreDimension = genreIndexMap.size();
        int totalDimension = tagDimension + genreDimension;

        INDArray vector = Nd4j.zeros(totalDimension);

        for(TagRelationship tagRelationship : mediaNode.getTags()) {
            String tagName = tagRelationship.getTag().getName();
            if (tagIndexMap.containsKey(tagName)) {
                int index = tagIndexMap.get(tagName);
                double relevance = tagRelationship.getRelevance() / 100.0;
                vector.putScalar(index, relevance);
            }
        }

        for (GenreNode genre : mediaNode.getGenres()) {
            String genreName = genre.getName();
            if (genreIndexMap.containsKey(genreName)) {
                int index = tagDimension + genreIndexMap.get(genreName);
                vector.putScalar(index, 1.0);
            }
        }

        double norm = vector.norm2Number().doubleValue();
        if (norm > 0) {
            vector.divi(norm);
        }

        double[] result = vector.toDoubleVector();

        log.debug("Generated vector for media '{}' with {} dimensions",
                mediaNode.getTitle(), totalDimension);

        return result;
    }
}
