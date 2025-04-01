package com.romaincaron.analyze.service.entity;

import com.romaincaron.analyze.entity.GenreNode;
import com.romaincaron.analyze.entity.MediaNode;

import java.util.List;
import java.util.Optional;

public interface GenreNodeService {
    Optional<GenreNode> findByName(String name);
    List<MediaNode> findMediaByGenre(String genreName);
    GenreNode save(GenreNode genreNode);
}
