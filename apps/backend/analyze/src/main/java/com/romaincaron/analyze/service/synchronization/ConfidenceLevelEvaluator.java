package com.romaincaron.analyze.service.synchronization;

public interface ConfidenceLevelEvaluator {
    String evaluateConfidenceLevel(String sourceName, Integer relevance);
}
