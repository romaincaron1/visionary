package com.romaincaron.analyze.service.synchronization.impl;

import com.romaincaron.analyze.dto.MediaDto;
import com.romaincaron.analyze.entity.GenreNode;
import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.service.entity.GenreNodeService;
import com.romaincaron.analyze.service.synchronization.GenreSynchronizer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DefaultGenreSynchronizer implements GenreSynchronizer {
    private static final Logger log = LoggerFactory.getLogger(DefaultGenreSynchronizer.class);

    private final GenreNodeService genreNodeService;

    @Override
    public void synchronize(MediaNode mediaNode, MediaDto mediaDto) {
        if (mediaDto.getGenres() == null || mediaDto.getGenres().isEmpty()) {
            mediaNode.getGenres().clear();
            return;
        }

        Set<GenreNode> currentGenres = new HashSet<>(mediaNode.getGenres());
        Set<GenreNode> updatedGenres = new HashSet<>();

        for (String genreName : mediaDto.getGenres()) {
            GenreNode genreNode = findOrCreateGenre(genreName);
            updatedGenres.add(genreNode);
        }

        // Remove obsolete relationships
        Set<GenreNode> toRemove = new HashSet<>(currentGenres);
        toRemove.removeAll(updatedGenres);
        if (!toRemove.isEmpty()) {
            mediaNode.getGenres().removeAll(toRemove);
            log.info("Removed {} obsolete genre relationships for {}",
                    toRemove.size(), mediaNode.getTitle());
        }

        // Add new relationships
        Set<GenreNode> toAdd = new HashSet<>(updatedGenres);
        toAdd.removeAll(currentGenres);
        for (GenreNode genre : toAdd) {
            mediaNode.getGenres().add(genre);
            log.info("Added new genre '{}' to '{}'", genre.getName(), mediaNode.getTitle());
        }
    }

    private GenreNode findOrCreateGenre(String genreName) {
        Optional<GenreNode> existingGenre = genreNodeService.findByName(genreName);

        if (existingGenre.isPresent()) {
            return existingGenre.get();
        } else {
            GenreNode newGenre = new GenreNode();
            newGenre.setName(genreName);
            GenreNode savedGenre = genreNodeService.save(newGenre);
            log.info("Created new genre: {}", genreName);
            return savedGenre;
        }
    }
}