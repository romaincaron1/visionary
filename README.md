# Visionary - Plateforme de Recommandation Basée sur le Contenu

Visionary est une plateforme sophistiquée de recommandation conçue pour fournir des suggestions personnalisées pour différents types de médias (mangas, animes, films, séries) basées sur la similarité de contenu. Le système analyse les caractéristiques des médias, crée des représentations vectorielles et détermine des similarités pour délivrer des recommandations précises et explicables.

## Vue d'ensemble de l'Architecture

Visionary est construit sur une architecture moderne de microservices avec les composants suivants :

### Microservices

1. **Service Eureka** - Serveur de découverte et d'enregistrement de services
2. **Service de Collecte de Données** - Récupère et persiste les données des médias depuis des API externes
3. **Service d'Analyse** - Transforme les données des médias en représentations vectorielles et calcule les similarités
4. **Service de Recommandation** (planifié) - Fournit des recommandations personnalisées utilisant divers algorithmes

## Technologies Principales

- **Spring Boot 3.4.x** - Framework d'application
- **Spring Cloud** - Coordination des microservices
- **Neo4j** - Base de données graphe pour la modélisation des relations
- **PostgreSQL** - Base de données relationnelle pour les données structurées
- **Apache Kafka** - Streaming d'événements pour la communication inter-services
- **Deeplearning4j** - Génération d'embeddings vectoriels
- **Docker** - Conteneurisation pour un déploiement facilité

## Fonctionnalités Principales

- Système de recommandation basé sur le contenu avec des résultats explicables
- Synchronisation efficace des données entre services utilisant une architecture événementielle
- Embeddings vectoriels pour la représentation sémantique du contenu des médias
- Modélisation des relations basée sur les graphes pour une précision accrue des recommandations
- Opérations atomiques avec support des transactions
- Mécanismes robustes de gestion et de récupération des erreurs

## Comment Ça Fonctionne

### Collecte et Traitement des Données

La plateforme suit ces étapes pour fournir des recommandations :

1. **Collecte de Données** : Récupère les métadonnées des médias depuis des API externes (AniList, etc.)
2. **Publication d'Événements** : Publie des événements quand un média est créé ou mis à jour
3. **Construction du Graphe** : Construit une représentation en graphe dans Neo4j avec des nœuds et des relations
4. **Vectorisation** : Convertit les caractéristiques des médias en vecteurs numériques
5. **Calcul de Similarité** : Détermine la similarité de contenu en utilisant la similarité cosinus
6. **Recommandation** : Délivre des recommandations personnalisées basées sur les scores de similarité

### Exemple de Représentation Vectorielle

Chaque média est représenté comme un vecteur à haute dimension qui capture son contenu sémantique :

```
Média : "Shutter Island"
Tags : "Thriller psychologique" (90%), "Mystère" (85%), "Suspense" (75%), "Hôpital psychiatrique" (65%)
Genres : "Thriller", "Drame", "Mystère"

Vecteur brut : [0.0, 0.0, 0.0, 0.9, 0.85, 0.75, 0.65, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0]
              |------------------ Tags --------------------|  |--- Genres ---|
              
Normalisé : [0.0, 0.0, 0.0, 0.39, 0.37, 0.33, 0.28, 0.0, 0.0, 0.44, 0.44, 0.44, 0.0]
```

Le vecteur combine :
- Les valeurs des tags avec leurs scores de pertinence (normalisés entre 0 et 1)
- Des indicateurs binaires de présence de genres (1.0 si présent, 0.0 si absent)
- Normalisation du vecteur (divisé par la norme L2) pour supporter les calculs de similarité cosinus

### Calcul de Similarité

La similarité entre les médias est calculée en utilisant la similarité cosinus de leurs vecteurs :

```
Similarité(A,B) = cos(θ) = (A·B)/(||A||·||B||)
```

Cela produit une valeur entre 0 (complètement différent) et 1 (identique), qui est utilisée pour classer les recommandations.

## Points d'API

### Service de Collecte de Données
- `GET /api/media/{id}` - Récupérer un média par ID
- `GET /api/media/update/all` - Déclencher une synchronisation complète des données

### Service d'Analyse
- `GET /api/sync/{mediaId}` - Synchroniser un média spécifique vers Neo4j

## Exemple Concret de Recommandation

Sur la base du vecteur généré pour "Shutter Island", le système pourrait recommander:

1. **"Inception"** - Similarité: 0.89
   * Tags communs: "Thriller psychologique", "Mystère"
   * Genres communs: "Thriller", "Drame"
   * Justification: Les deux films explorent des réalités altérées et questionnent la perception.

2. **"Memento"** - Similarité: 0.87
   * Tags communs: "Thriller psychologique", "Mystère", "Suspense"
   * Genres communs: "Thriller", "Mystère"
   * Justification: Structure narrative complexe autour de la mémoire et de l'identité.

3. **"Le Silence des Agneaux"** - Similarité: 0.82
   * Tags communs: "Thriller psychologique", "Suspense"
   * Genres communs: "Thriller", "Drame"
   * Justification: Profondeur psychologique et tension narrative similaires.

Cette approche permet non seulement de fournir des recommandations pertinentes, mais aussi d'expliquer clairement pourquoi ces recommandations ont été faites, rendant le système transparent pour l'utilisateur.