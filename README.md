# CiviAI

**Plateforme intelligente de signalement et de gestion des problèmes urbains, propulsée par l'intelligence artificielle.**

CiviAI connecte l'alerte citoyenne à l'action municipale : les citoyens signalent un problème (route endommagée, éclairage en panne, fuite d'eau, déchets), l'IA regroupe et classe les signalements, et la municipalité intervient. Le projet est réalisé par l'équipe **InnovaSix** et repose sur **6 microservices**.

## Vision

- **Analyse par IA** : classification automatique des signalements à partir des photos et des descriptions.
- **Mobilisation collective** : les riverains confirment les problèmes, ce qui permet de qualifier leur impact.
- **Transparence totale** : suivi en temps réel des travaux et évaluation citoyenne après intervention.

## Parcours d'un signalement

1. **Signaler** : description, photo et localisation GPS.
2. **Analyser** : classification par IA et détection des doublons.
3. **Confirmer** : soutien des riverains concernés.
4. **Orienter** : routage vers le service compétent.
5. **Intervenir** : affectation d'une équipe et réalisation des travaux.
6. **Évaluer** : notation citoyenne ou réclamation.

## Architecture

Le système s'organise en cinq couches autonomes :

1. **Présentation** : application citoyenne et dashboard municipal.
2. **Entrée API** : API Gateway centralisée (routage et sécurité JWT).
3. **Services métier** : 6 microservices Spring Boot.
4. **Messagerie** : bus d'événements RabbitMQ.
5. **Persistance** : une base MySQL par microservice.

## Les 6 microservices

| Service | Rôle | Responsable |
|---|---|---|
| User Service | Identité, comptes, rôles et sécurité | Safa Banattia |
| Report Service | Collecte des signalements, photos et cartes | Youssef Saaidi |
| Incident Service | Regroupement des signalements et priorité | Molka Jebali |
| Municipal Management | Municipalités, départements et routage | Zeineb Arfaoui |
| Intervention Service | Planification et suivi des travaux | Yessine Eleuch |
| Citizen Engagement | Confirmations, commentaires et évaluations | Amen Allah Abdelhafidh |

Chaque service est documenté dans le README de son dossier.

## Diagramme de classes

Chaque service possède ses propres entités et sa propre base de données. Les références entre services (flèches `→`) se font par identifiant, sans clé étrangère inter-base.

![Diagramme de classes global](Documentation/Diagrams/CiviAI_Diagramme_Classes_Global.png)

## Communication entre services

Les services échangent par **API REST** pour les réponses immédiates et par **événements RabbitMQ** pour les traitements asynchrones. Exemple : `ReportCreated` → `ReportLinkedToIncident` → `IncidentAssignedToDepartment` → `InterventionCompleted` → `FeedbackSubmitted`.

## Technologies

- **Backend** : Java, Spring Boot, REST et OpenAPI/Swagger.
- **Échanges** : OpenFeign (synchrone) et RabbitMQ (asynchrone).
- **Données et sécurité** : MySQL, Spring Security et JWT.
- **Cartes et médias** : OpenStreetMap et Cloudinary.
- **Déploiement** : Docker.

## Règles de conception

- **Database per Service** : aucun accès direct aux tables d'un autre service.
- **Résilience** : messages persistants et consommateurs idempotents.
- **Traçabilité** : suppression logique et historique des actions sensibles.
- **Accès sécurisés** : droits contrôlés par rôle et par périmètre municipal.

## Feuille de route

1. **Phase 1 · Socle métier** : les six services CRUD avec leurs règles métier.
2. **Phase 2 · Intégration** : API Gateway, sécurité JWT et bus RabbitMQ.
3. **Phase 3 · Intelligence** : module IA de classification, cartes thermiques et maintenance prédictive.

## Organisation du dépôt

- Monorepo modulaire : un dossier par service.
- Branches : `main` (stable), `develop` (intégration) et `feature/<service>`.
- Pull Requests obligatoires avec revue croisée.
- Commits normés : `feat:`, `fix:`, `docs:`.
