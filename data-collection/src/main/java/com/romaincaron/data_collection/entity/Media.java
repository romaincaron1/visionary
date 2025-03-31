package com.romaincaron.data_collection.entity;

import com.romaincaron.data_collection.enums.MediaStatus;
import com.romaincaron.data_collection.enums.MediaType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "medias", indexes = {
        @Index(name = "idx_media_type", columnList = "media_type")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String externalId;

    @Column(name = "title_alternative")
    private String titleAlternative;

    @Column(name = "media_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType mediaType; // MANGA, ANIME, MOVIE, SERIES, etc.

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    private String author;

    private String artist;

    @Column(name = "start_year")
    private Integer startYear;

    @Column(name = "end_year")
    private Integer endYear;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MediaStatus status; // ONGOING, COMPLETED, HIATUS

    @Column(name = "cover_url")
    private String coverUrl;

    private Double rating;

    private Integer popularity;

    @Column(name = "source_name")
    private String sourceName;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "media_genres",
            joinColumns = @JoinColumn(name = "media_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @OneToMany(mappedBy = "media", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MediaTag> mediaTags = new HashSet<>();
}
