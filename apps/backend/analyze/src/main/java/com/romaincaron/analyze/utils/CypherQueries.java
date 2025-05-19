package com.romaincaron.analyze.utils;

public class CypherQueries {

    /**
     * Trouve les médias similaires de manière optimisée
     */
    public static final String FIND_SIMILAR_MEDIA = """
        // Match initial avec le média source et collecte des tags/genres en une seule passe
        MATCH (m:MediaNode {externalId: $externalId})
        CALL {
            WITH m
            MATCH (m)-[:HAS_TAG]->(t:TagNode)
            WITH t, COUNT {
                MATCH ()-[:HAS_TAG]->(t)
            } as usage
            WHERE 15 <= usage <= 50
            RETURN collect(t) as relevantTags
        }
        WITH m, relevantTags
        MATCH (m)-[:HAS_GENRE]->(g:GenreNode)
        WITH m, relevantTags, collect(g) as genres
        WHERE size(relevantTags) >= 2 AND size(genres) >= 1
        
        // Recherche rapide des candidats potentiels
        MATCH (m2:MediaNode)
        WHERE m2 <> m 
        AND ($mediaType IS NULL OR m2.mediaType = $mediaType)
        WITH m2, relevantTags, genres
        LIMIT 200
        
        // Calcul optimisé des scores avec filtrage précoce
        MATCH (m2)-[:HAS_TAG]->(t:TagNode)
        WHERE t IN relevantTags
        WITH m2, relevantTags, genres, count(t) as tagMatches
        WHERE tagMatches >= 2
        
        // Calcul final du score avec les genres
        MATCH (m2)-[:HAS_GENRE]->(g:GenreNode)
        WHERE g IN genres
        WITH m2, 
             tagMatches * 0.7 + count(g) * 0.3 as score,
             tagMatches as commonTagCount,
             count(g) as commonGenreCount
        WHERE score >= 1.5
        
        RETURN m2.externalId as mediaId,
               m2.title as title,
               m2.mediaType as mediaType,
               score as similarity,
               commonGenreCount,
               commonTagCount
        ORDER BY score DESC
        LIMIT $limit
        """;
}
