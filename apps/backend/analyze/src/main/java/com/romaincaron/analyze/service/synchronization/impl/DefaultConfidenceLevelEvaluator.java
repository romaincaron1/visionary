package com.romaincaron.analyze.service.synchronization.impl;

import com.romaincaron.analyze.service.synchronization.ConfidenceLevelEvaluator;
import org.springframework.stereotype.Service;

@Service
public class DefaultConfidenceLevelEvaluator implements ConfidenceLevelEvaluator {

    @Override
    public String evaluateConfidenceLevel(String sourceName, Integer relevance) {
        // Default to medium confidence
        String level = "MEDIUM";

        // If relevance is missing, low confidence
        if (relevance == null) {
            return "LOW";
        }

        // Source-specific logic
        switch (sourceName.toLowerCase()) {
            case "anilist":
                // AniList has generally good quality tags
                if (relevance > 75) {
                    level = "HIGH";
                } else if (relevance < 30) {
                    level = "LOW";
                }
                break;

            case "tvtropes":
                // TVTropes might have different threshold
                if (relevance > 85) {
                    level = "HIGH";
                } else if (relevance < 40) {
                    level = "LOW";
                }
                break;

            default:
                // Unknown sources - be more conservative
                if (relevance > 90) {
                    level = "HIGH";
                } else if (relevance < 50) {
                    level = "LOW";
                }
        }

        return level;
    }
}