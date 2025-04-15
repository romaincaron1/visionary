package com.romaincaron.analyze.utils;

public class CypherQueries {

    /**
     * Find similar media
     */
    public static final String FIND_SIMILAR_MEDIA =
            "MATCH (source:MediaNode) WHERE source.externalId = $mediaId AND source.sourceName = $sourceName " +
                    "MATCH (other:MediaNode) WHERE other.externalId <> $mediaId OR other.sourceName <> $sourceName " +

                    "OPTIONAL MATCH (source)-[:HAS_GENRE]->(g:GenreNode)<-[:HAS_GENRE]-(other) " +
                    "WITH source, other, count(g) AS commonGenres " +

                    "MATCH (source)-[:HAS_GENRE]->(g1:GenreNode) " +
                    "WITH source, other, commonGenres, count(g1) AS genres1 " +
                    "MATCH (other)-[:HAS_GENRE]->(g2:GenreNode) " +
                    "WITH source, other, commonGenres, genres1, count(g2) AS genres2 " +

                    "OPTIONAL MATCH (source)-[r1:HAS_TAG]->(t:TagNode)<-[r2:HAS_TAG]-(other) " +
                    "WITH source, other, commonGenres, genres1, genres2, " +
                    "COLLECT({tag: t.name, relevance1: r1.relevance, relevance2: r2.relevance}) AS commonTags " +

                    "MATCH (source)-[:HAS_TAG]->(t1:TagNode) " +
                    "WITH source, other, commonGenres, genres1, genres2, commonTags, count(t1) AS tags1 " +
                    "MATCH (other)-[:HAS_TAG]->(t2:TagNode) " +
                    "WITH other, commonGenres, genres1, genres2, commonTags, tags1, count(t2) AS tags2 " +

                    "WITH other, " +
                    "CASE WHEN (genres1 + genres2 - commonGenres) > 0 " +
                    "THEN 1.0 * commonGenres / (genres1 + genres2 - commonGenres) " +
                    "ELSE 0 " +
                    "END AS genreSimilarity, " +

                    "CASE WHEN (tags1 + tags2) > 0 " +
                    "THEN REDUCE(s = 0.0, t IN commonTags | s + (t.relevance1 * t.relevance2) / 10000.0) / (tags1 + tags2) * 2 " +
                    "ELSE 0 " +
                    "END AS tagSimilarity, " +

                    "size(commonTags) AS commonTagCount, " +
                    "commonGenres, commonTags " +

                    "WITH other, " +
                    "tagSimilarity * 0.7 + genreSimilarity * 0.3 AS similarity, " +
                    "commonGenres, genreSimilarity, commonTagCount, tagSimilarity, commonTags " +

                    "WHERE similarity > 0.1 " +

                    "RETURN other.externalId AS mediaId, other.sourceName AS sourceName, " +
                    "other.title AS title, other.mediaType AS mediaType, " +
                    "similarity, commonGenres, genreSimilarity, commonTagCount, tagSimilarity, " +
                    "[t IN commonTags | t.tag] AS commonTagNames " +
                    "ORDER BY similarity DESC " +
                    "LIMIT $limit";

}
