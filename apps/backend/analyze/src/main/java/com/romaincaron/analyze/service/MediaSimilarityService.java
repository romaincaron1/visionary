package com.romaincaron.analyze.service;

import com.romaincaron.analyze.dto.MediaSimilarityResultDto;
import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.utils.CypherQueries;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MediaSimilarityService {

    private final Driver neo4jDriver;

    /**
     * Calculate Cosine similarity between one media and another one
     * @param media1
     * @param media2
     * @return
     */
    public double calculateCosineSimilarity(MediaNode media1, MediaNode media2) {
        double[] vector1 = media1.getVectorAsDoubleArray();
        double[] vector2 = media2.getVectorAsDoubleArray();

        if (vector1.length == 0 || vector2.length == 0 || vector1.length != vector2.length) {
            log.warn("Cannot calculate similarity: incompatible vectors");
            return 0.0;
        }

        INDArray v1 = Nd4j.create(vector1);
        INDArray v2 = Nd4j.create(vector2);

        // Calculate similarity: dot(v1, v2) / (||v1|| * ||v2||)
        double dotProduct = v1.mul(v2).sumNumber().doubleValue();
        double norm1 = v1.norm2Number().doubleValue();
        double norm2 = v2.norm2Number().doubleValue();

        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }

        return dotProduct / (norm1 * norm2);
    }

    public List<MediaSimilarityResultDto> findSimilarMedia(String externalId, String sourceName, int limit) {
        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    CypherQueries.FIND_SIMILAR_MEDIA,
                    Values.parameters(
                            "mediaId", externalId,
                            "sourceName", sourceName,
                            "limit", limit
                    )
            );
            return result.list().stream()
                    .map(record -> new MediaSimilarityResultDto(
                            record.get("mediaId").asString(),
                            record.get("sourceName").asString(),
                            record.get("title").asString(),
                            record.get("mediaType").asString(),
                            record.get("similarity").asDouble(),
                            record.get("commonGenres").asInt(),
                            record.get("genreSimilarity").asDouble(),
                            record.get("commonTagCount").asInt(),
                            record.get("tagSimilarity").asDouble(),
                            record.get("commonTagNames").asList(value -> value.asString())
                    ))
                    .collect(Collectors.toList());
        }
    }


}
