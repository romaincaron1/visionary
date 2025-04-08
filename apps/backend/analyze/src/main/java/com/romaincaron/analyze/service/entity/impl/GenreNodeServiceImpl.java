package com.romaincaron.analyze.service.entity.impl;

import com.romaincaron.analyze.entity.GenreNode;
import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.repositories.GenreNodeRepository;
import com.romaincaron.analyze.service.entity.GenreNodeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GenreNodeServiceImpl implements GenreNodeService {

    private final GenreNodeRepository genreNodeRepository;

    @Override
    public Optional<GenreNode> findByName(String name) {
        return genreNodeRepository.findByName(name);
    }

    @Override
    public List<MediaNode> findMediaByGenre(String genreName) {
        return genreNodeRepository.findMediaByGenre(genreName);
    }

    @Override
    public GenreNode save(GenreNode genreNode) {
        return genreNodeRepository.save(genreNode);
    }

    @Override
    public List<GenreNode> findAll() {
        return genreNodeRepository.findAll();
    }
}
