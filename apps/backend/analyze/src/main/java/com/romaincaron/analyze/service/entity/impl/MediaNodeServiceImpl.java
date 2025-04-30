package com.romaincaron.analyze.service.entity.impl;

import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.repositories.MediaNodeRepository;
import com.romaincaron.analyze.service.entity.MediaNodeService;
import lombok.AllArgsConstructor;
import org.neo4j.driver.types.Type;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class MediaNodeServiceImpl implements MediaNodeService {

    private final MediaNodeRepository mediaNodeRepository;
    private final Driver neo4jDriver;

    @Override
    public Optional<MediaNode> findByExternalIdAndSourceName(String externalId, String sourceName) {
        return mediaNodeRepository.findByExternalIdAndSourceName(externalId, sourceName);
    }

    @Override
    public List<MediaNode> findMediaWithSimilarGenres(String externalId, String sourceName, int limit) {
        return mediaNodeRepository.findMediaWithSimilarGenres(externalId, sourceName, limit);
    }

    @Override
    public List<MediaNode> findMediaWithSimilarTags(String externalId, String sourceName, int limit) {
        return mediaNodeRepository.findMediaWithSimilarTags(externalId, sourceName, limit);
    }

    @Override
    public List<MediaNode> findAll() {
        return mediaNodeRepository.findAll();
    }

    @Override
    public Optional<MediaNode> findById(Long id) {
        return mediaNodeRepository.findById(id);
    }

    @Override
    public Page<MediaNode> findAllPaginated(Pageable pageable) {
        return mediaNodeRepository.findAll(pageable);
    }

    public MediaNode save(MediaNode mediaNode) {
        return mediaNodeRepository.save(mediaNode);
    }

    public Map<String, Object> getVectorStats() {
        Map<String, Object> result = new HashMap<>();

        try (Session session = neo4jDriver.session()) {
            // Exécuter la requête directement avec le driver Neo4j
            String query = "MATCH (m:MediaNode) " +
                    "WITH size(m.contentVector) AS vectorSize " +
                    "RETURN min(vectorSize) AS minSize, " +
                    "max(vectorSize) AS maxSize, " +
                    "avg(vectorSize) AS avgSize, " +
                    "count(vectorSize) AS totalNodes";

            Record record = session.run(query).single();

            if (record != null) {
                int minSize = record.get("minSize").asInt();
                int maxSize = record.get("maxSize").asInt();
                double avgSize = record.get("avgSize").asDouble();
                long totalNodes = record.get("totalNodes").asLong();

                result.put("minSize", minSize);
                result.put("maxSize", maxSize);
                result.put("avgSize", avgSize);
                result.put("totalNodes", totalNodes);
                result.put("allSame", minSize == maxSize);
            } else {
                result.put("error", "No MediaNodes found");
            }
        } catch (Exception e) {
            result.put("error", "Error executing query: " + e.getMessage());
        }

        return result;
    }

    @Override
    public List<Map<String, String>> findAllMediaIdentifiers(int skip, int limit) {
        try (Session session = neo4jDriver.session()) {
            String query = "MATCH (m:MediaNode) " +
                    "RETURN m.externalId AS externalId, m.sourceName AS sourceName " +
                    "ORDER BY m.externalId " +
                    "SKIP $skip LIMIT $limit";

            Result result = session.run(query, Map.of("skip", skip, "limit", limit));

            List<Map<String, String>> mediaIdentifiers = new ArrayList<>();
            while (result.hasNext()) {
                Record record = result.next();
                Map<String, String> identifier = new HashMap<>();

                if (!record.get("externalId").isNull()) {
                    identifier.put("externalId", record.get("externalId").asString());
                }

                if (!record.get("sourceName").isNull()) {
                    identifier.put("sourceName", record.get("sourceName").asString());
                }

                if (!identifier.isEmpty()) {
                    mediaIdentifiers.add(identifier);
                }
            }

            return mediaIdentifiers;
        }
    }
}
