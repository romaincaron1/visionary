# Visionary - Media Recommendation Platform

Visionary is a sophisticated recommendation platform that delivers personalized suggestions for various media types (manga, anime, movies, TV series) based on content similarity. The system analyzes media characteristics, creates vector representations, and determines similarities to provide accurate and explainable recommendations.

## Architecture Overview

Built on a modern microservices architecture:

- **Eureka Service**: Service discovery and registration
- **Data Collection Service**: Retrieves and persists media data from external APIs
- **Analysis Service**: Transforms media data into vector representations and calculates similarities
- **Recommendation Service** (planned): Provides personalized recommendations using various algorithms

## Core Technologies

- Spring Boot 3.4
- Spring Cloud
- Neo4j (graph database)
- PostgreSQL
- Apache Kafka
- Deeplearning4j
- Docker

## Key Features

- Content-based recommendation system with explainable results
- Efficient data synchronization between services using event-driven architecture
- Vector embeddings for semantic representation of media content
- Graph-based relationship modeling for improved recommendation accuracy
- Atomic operations with transaction support
- Robust error handling and recovery mechanisms

## How It Works

### Data Pipeline

1. **Data Collection**: Retrieves media metadata from external APIs (AniList, etc.)
2. **Event Publishing**: Publishes events when media is created or updated
3. **Graph Construction**: Builds a graph representation in Neo4j with nodes and relationships
4. **Vectorization**: Converts media characteristics into numerical vectors
5. **Similarity Calculation**: Determines content similarity using cosine similarity
6. **Recommendation**: Delivers personalized recommendations based on similarity scores

### Vector Representation Example

Each media is represented as a high-dimensional vector capturing its semantic content:

```
Media: "Shutter Island"
Tags: "Psychological thriller" (90%), "Mystery" (85%), "Suspense" (75%), "Psychiatric hospital" (65%)
Genres: "Thriller", "Drama", "Mystery"

Raw vector: [0.0, 0.0, 0.0, 0.9, 0.85, 0.75, 0.65, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0]
           |------------------ Tags --------------------|  |--- Genres ---|
           
Normalized: [0.0, 0.0, 0.0, 0.39, 0.37, 0.33, 0.28, 0.0, 0.0, 0.44, 0.44, 0.44, 0.0]
```

### Similarity Calculation

Similarity between media is calculated using cosine similarity of their vectors:

```
Similarity(A,B) = cos(θ) = (A·B)/(||A||·||B||)
```

This produces a value between 0 (completely different) and 1 (identical), used to rank recommendations.

### Recommendation Example

Based on "Shutter Island", the system might recommend:

- **"Inception"** - Similarity: 0.89
   - Common tags: "Psychological thriller", "Mystery"
   - Common genres: "Thriller", "Drama"

- **"Memento"** - Similarity: 0.87
   - Common tags: "Psychological thriller", "Mystery", "Suspense"
   - Common genres: "Thriller", "Mystery"

This approach provides relevant recommendations while clearly explaining why they were made, making the system transparent to users.