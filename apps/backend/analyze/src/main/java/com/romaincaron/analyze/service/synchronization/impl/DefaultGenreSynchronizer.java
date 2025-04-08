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
        if (mediaNode.getGenres() == null) {
            mediaNode.setGenres(new HashSet<>());
        }

        Set<GenreNode> currentGenres = new HashSet<>(mediaNode.getGenres());
        Set<GenreNode> updatedGenres = new HashSet<>();

        for (GenreDto genreDto : mediaDto.getGenres()) {
            GenreNode genreNode = findOrCreateGenre(genreDto);
            updatedGenres.add(genreNode);
        }

        // Remove obsolete relationships
        Set<GenreNode> toRemove = new HashSet<>(currentGenres);
        toRemove.removeAll(updatedGenres);
        if (!toRemove.isEmpty()) {
            mediaNode.getGenres().removeAll(toRemove);
            log.info("Removed obsoletes genre relationships for {}",
                    mediaNode.getTitle());
        }

        // Add new relationships
        Set<GenreNode> toAdd = new HashSet<>(updatedGenres);
        toAdd.removeAll(currentGenres);
        for (GenreNode genre : toAdd) {
            mediaNode.getGenres().add(genre);
            log.info("Added new genre '{}' to '{}'", genre.getName(), mediaNode.getTitle());
        }
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
            log.info("Created new genre: {}", genreDto.getName());
            return savedGenre;
        }
    }
}