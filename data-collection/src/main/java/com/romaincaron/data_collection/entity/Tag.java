package com.romaincaron.data_collection.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@Getter
@Setter
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "source_name")
    private String sourceName;

    @OneToMany(mappedBy = "tag")
    private Set<MediaTag> mediaTags = new HashSet<>();
}
