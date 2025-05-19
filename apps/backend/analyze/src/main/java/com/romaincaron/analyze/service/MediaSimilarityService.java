package com.romaincaron.analyze.service;

import com.romaincaron.analyze.dto.MediaSimilarityResultDto;
import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.enums.MediaType;
import com.romaincaron.analyze.utils.CypherQueries;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MediaSimilarityService {

    private final Driver neo4jDriver;

    /**
     * Trouve les médias similaires à un média donné
     * @param mediaNode Le média de référence
     * @param limit Nombre maximum de résultats
     * @param mediaType Type de média
     * @return Liste des médias similaires avec leurs scores
     */
    public List<MediaSimilarityResultDto> findSimilarMedia(
            MediaNode mediaNode,
            int limit,
            MediaType mediaType) {
        try (Session session = neo4jDriver.session()) {
            Map<String, Object> params = Map.of(
                "externalId", mediaNode.getExternalId(),
                "limit", limit,
                "mediaType", mediaType != null ? mediaType.name() : null
            );

            List<MediaSimilarityResultDto> results = new ArrayList<>();
            var result = session.run(CypherQueries.FIND_SIMILAR_MEDIA, params);
            
            while (result.hasNext()) {
                Record record = result.next();
                MediaSimilarityResultDto dto = new MediaSimilarityResultDto(
                    record.get("mediaId").asString(),
                    record.get("title").asString(),
                    MediaType.valueOf(record.get("mediaType").asString()),
                    record.get("similarity").asDouble(),
                    record.get("commonGenreCount").asInt(),
                    record.get("commonTagCount").asInt()
                );
                results.add(dto);
            }

            return results;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Error finding similar media for {}: {}", 
                        mediaNode.getTitle(), e.getMessage());
            }
            return List.of();
        }
    }
}
