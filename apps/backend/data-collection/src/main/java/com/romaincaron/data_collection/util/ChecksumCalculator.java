package com.romaincaron.data_collection.util;

import com.romaincaron.data_collection.dto.MediaDto;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChecksumCalculator {

    public static String calculateFromDto(MediaDto mediaDto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            updateDigestWithString(digest, mediaDto.getTitle());
            updateDigestWithString(digest, mediaDto.getExternalId());
            updateDigestWithString(digest, mediaDto.getTitleAlternative());
            updateDigestWithString(digest, mediaDto.getSynopsis());
            updateDigestWithString(digest, mediaDto.getMediaType() != null ?
                    mediaDto.getMediaType().toString() : null);
            updateDigestWithInt(digest, mediaDto.getStartYear());
            updateDigestWithInt(digest, mediaDto.getEndYear());
            updateDigestWithString(digest, mediaDto.getAuthor());
            updateDigestWithString(digest, mediaDto.getArtist());
            updateDigestWithString(digest, mediaDto.getStatus() != null ?
                    mediaDto.getStatus().name() : null);
            updateDigestWithString(digest, mediaDto.getCoverUrl());
            updateDigestWithDouble(digest, mediaDto.getRating());
            updateDigestWithInt(digest, mediaDto.getPopularity());
            updateDigestWithString(digest, mediaDto.getSourceName());

            return convertDigestToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private static void updateDigestWithString(MessageDigest digest, String value) {
        if (value != null) {
            digest.update(value.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static void updateDigestWithInt(MessageDigest digest, Integer value) {
        if (value != null) {
            digest.update(ByteBuffer.allocate(4).putInt(value).array());
        }
    }

    private static void updateDigestWithDouble(MessageDigest digest, Double value) {
        if (value != null) {
            digest.update(ByteBuffer.allocate(8).putDouble(value).array());
        }
    }

    private static String convertDigestToHex(MessageDigest digest) {
        byte[] hashBytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}