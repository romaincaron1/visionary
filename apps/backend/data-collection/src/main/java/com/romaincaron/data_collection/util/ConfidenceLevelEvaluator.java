package com.romaincaron.data_collection.util;

import com.romaincaron.data_collection.enums.ConfidenceLevel;

public class ConfidenceLevelEvaluator {

    /**
     * Set the confidence level based on the relevance
     * @param relevance
     * @return
     */
    public static ConfidenceLevel EVALUATE(Integer relevance) {
        ConfidenceLevel level = ConfidenceLevel.MEDIUM;

        if (relevance > 75) level = ConfidenceLevel.HIGH;
        if (relevance < 50) level = ConfidenceLevel.LOW;

        return level;
    }

}
