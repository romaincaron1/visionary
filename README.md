# VISIONARY - Système de Recommandation Intelligent

## Présentation

Visionary est une plateforme de recommandation qui propose aux utilisateurs des œuvres culturelles (mangas, animes, films, séries) correspondant à leurs préférences. Sa particularité réside dans sa capacité à expliquer les raisons derrière chaque recommandation et à découvrir des similitudes subtiles entre œuvres grâce à l'analyse vectorielle avancée.

## Architecture

Le système s'appuie sur une architecture microservices performante :

- **Service de découverte (Eureka)** : Enregistre tous les microservices et facilite leur découverte mutuelle
- **Service de collecte de données** : Acquisition d'informations détaillées depuis diverses sources
- **Service d'analyse** : Traitement des données avec graphes et intelligence artificielle
- **Service de recommandation** : Génération de suggestions personnalisées

La communication entre ces services est assurée par **Feign Client**, qui simplifie les appels REST entre microservices avec une approche déclarative.

## Technologies clés

- **Spring Boot & Spring Cloud** : Framework pour le développement des microservices
- **Eureka Server** : Service de découverte pour l'orchestration des microservices
- **Feign Client** : Client HTTP déclaratif pour la communication inter-services
- **Neo4j** : Base de données en graphe pour modéliser les relations complexes
- **PostgreSQL** : Stockage structuré des données collectées
- **Deeplearning4j** : Analyse vectorielle et embeddings sémantiques
- **Redis** : Cache hautes performances pour les recommandations
- **Kafka** : Communication asynchrone entre services

## Fonctionnement

1. **Collecte** : Agrégation des métadonnées depuis plusieurs sources (informations, genres, tags)

2. **Modélisation en graphe** : Construction d'un réseau de relations entre œuvres dans Neo4j

3. **Analyse vectorielle** : Génération de représentations mathématiques captant l'essence des œuvres

4. **Calcul de similarité multi-dimensionnel** : Combinaison d'analyses de graphe et vectorielles

5. **Recommandation contextualisée** : Suggestions adaptées avec explications détaillées

## Points forts techniques

- **Architecture microservices robuste** : Grâce à Eureka et Feign Client, les services peuvent être déployés, mis à l'échelle et mis à jour indépendamment
- **Communication simplifiée** : Feign Client transforme les appels REST entre services en simples appels de méthodes Java
- **Résilience intégrée** : Circuit breakers et retry mechanisms pour assurer la stabilité du système
- **Approche hybride** : Fusion intelligente des technologies de graphe et d'intelligence artificielle
- **Transparence algorithmique** : Explications claires du processus de recommandation

## Exemple d'utilisation

1. L'utilisateur indique apprécier "Inception", "Interstellar" et "The Matrix"
2. Le système analyse ces films dans son graphe de connaissances et leurs profils vectoriels
3. Il identifie des œuvres présentant des similitudes significatives
4. Il fournit des recommandations détaillées comme :
   > "Vous devriez apprécier 'Arrival' car ce film partage avec 'Inception' une narration non-linéaire, aborde des thèmes philosophiques profonds comme vos films préférés, et présente une réalisation visuelle remarquable tout en explorant des concepts scientifiques complexes."

## Schéma d'architecture

Un schéma détaillé de l'architecture et du flux de données est disponible sur notre dépôt GitHub dans le fichier `docs/architecture-diagram.png`. Ce schéma illustre les interactions entre les différents services, la façon dont Eureka coordonne la découverte des services, et comment Feign Client facilite la communication entre eux.

Visionary transforme l'expérience de découverte culturelle en combinant la puissance des technologies modernes avec une approche centrée sur l'explication, permettant aux utilisateurs de comprendre pourquoi certaines œuvres pourraient leur plaire et d'explorer de nouveaux horizons culturels avec confiance.