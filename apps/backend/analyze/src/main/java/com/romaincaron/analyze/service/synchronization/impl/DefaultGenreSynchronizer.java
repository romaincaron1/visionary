package com.romaincaron.analyze.service.synchronization.impl;

import com.romaincaron.analyze.dto.GenreDto;
import com.romaincaron.analyze.dto.MediaDto;
import com.romaincaron.analyze.entity.GenreNode;
import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.service.entity.GenreNodeService;
import com.romaincaron.analyze.service.synchronization.GenreSynchronizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultGenreSynchronizer implements GenreSynchronizer {

    private final GenreNodeService genreNodeService;

    @Override
    public void synchronize(MediaNode mediaNode, MediaDto mediaDto) {
        Set<GenreNode> newGenres = new HashSet<>();

        if (mediaDto.getGenres() != null && !mediaDto.getGenres().isEmpty()) {
            for (GenreDto genreDto : mediaDto.getGenres()) {
                GenreNode genreNode = findOrCreateGenre(genreDto);
                newGenres.add(genreNode);
            }
        }

        int removedCount = 0;
        if (mediaNode.getGenres() != null) {
            removedCount = mediaNode.getGenres().size();
        }

        if (mediaNode.getGenres() == null) {
            mediaNode.setGenres(new HashSet<>());
        } else {
            Set<GenreNode> existingGenres = new HashSet<>(mediaNode.getGenres());
            Set<GenreNode> addedGenres = new HashSet<>(newGenres);
            addedGenres.removeAll(existingGenres);

            if (log.isInfoEnabled()) {
                for (GenreNode genre : addedGenres) {
                    log.info("Will add new genre '{}' to '{}'", genre.getName(), mediaNode.getTitle());
                }
            }

            removedCount = existingGenres.size() - (newGenres.size() - addedGenres.size());
        }

        if (removedCount > 0 && log.isInfoEnabled()) {
            log.info("Removing {} obsolete genre relationships for {}", removedCount, mediaNode.getTitle());
        }

        mediaNode.getGenres().clear();
        mediaNode.getGenres().addAll(newGenres);
    }

    @Transactional
    protected GenreNode findOrCreateGenre(GenreDto genreDto) {
        Optional<GenreNode> existingGenre = genreNodeService.findByName(genreDto.getName());

        if (existingGenre.isPresent()) {
            return existingGenre.get();
        } else {
            GenreNode newGenre = new GenreNode();
            newGenre.setName(genreDto.getName());
            GenreNode savedGenre = genreNodeService.save(newGenre);
            if (log.isInfoEnabled()) {
                log.info("Created new genre: {}", genreDto.getName());
            }
            return savedGenre;
        }
    }
}