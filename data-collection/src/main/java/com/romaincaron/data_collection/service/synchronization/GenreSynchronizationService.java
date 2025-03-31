package com.romaincaron.data_collection.service.synchronization;

import com.romaincaron.data_collection.entity.Genre;
import com.romaincaron.data_collection.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreSynchronizationService {
    private final GenreRepository genreRepository;

    public Set<Genre> syncGenres(Set<String> genreNames) {
        Set<Genre> genres = new HashSet<>();
        for (String genreName : genreNames) {
            Genre genre = genreRepository.findByName(genreName)
                    .orElseGet(() -> {
                        Genre newGenre = new Genre();
                        newGenre.setName(genreName);
                        return genreRepository.save(newGenre);
                    });
            genres.add(genre);
        }
        return genres;
    }
}
