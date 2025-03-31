package com.romaincaron.analyze.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.romaincaron.analyze.dto.MediaDto;
import com.romaincaron.analyze.dto.MediaVectorDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataCollectionService {

    private final DataCollectionClient dataClient;
    private final ObjectMapper objectMapper;

    public MediaDto getMediaById(Long id) {
        try {
            return dataClient.getMediaById(id);
        } catch (Exception e) {
            log.error("Error fetching media with id {}: {}", id, e.getMessage());
            throw e;
        }
    }

    public MediaVectorDto getMediaVector(Long mediaId) {
        try {
            return dataClient.getMediaVectorByMediaId(mediaId);
        } catch (Exception e) {
            log.error("Vector not found for media {}: {}", mediaId, e.getMessage());
            return null;
        }
    }

    public MediaVectorDto saveMediaVector(Long mediaId, double[] vector) {
        try {
            String vectorJson = convertVectorToJson(vector);

            MediaVectorDto vectorDto = new MediaVectorDto();
            vectorDto.setMediaId(mediaId);
            vectorDto.setTagVector(vectorJson);
            vectorDto.setUpdatedAt(LocalDateTime.now());

            return dataClient.saveMediaVector(vectorDto);
        } catch (Exception e) {
            log.error("Error saving vector for media {}: {}", mediaId, e.getMessage());
            throw e;
        }
    }

    private String convertVectorToJson(double[] vector) {
        try {
            return objectMapper.writeValueAsString(vector);
        } catch (Exception e) {
            log.error("Error converting vector to JSON: {}", e.getMessage());
            throw new RuntimeException("Failed to convert vector to JSON", e);
        }
    }
}
