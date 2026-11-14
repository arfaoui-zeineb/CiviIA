# Tableau Kanban — CiviIA

Pilotage du projet **CiviIA** (plateforme à 6 microservices) pour la séance 4.
Le board est la référence pour l'organisation du travail ; il est synchronisé avec les
issues GitHub et mis à jour à chaque séance.

| Paramètre | Valeur |
|---|---|
| **Outil** | GitHub Projects (vue Kanban) / Trello |
| **Cadence** | Sprints hebdomadaires, alignés sur les séances de suivi |
| **Convention de commits** | `feat:`, `fix:`, `docs:` |
| **Revue** | Pull Request obligatoire avec relecture croisée |
| **Légende** | `P1` priorité haute · `P2` moyenne · `P3` basse — `(Membre)` responsable |

## Board — Sprint 1 (13 → 20 novembre 2026)

| Backlog | À faire | En cours | Review | Done |
|---|---|---|---|---|
| `P2` Classification IA des signalements (Molka)<br>`P2` Détection de doublons GPS + sémantique (Molka)<br>`P2` Recalcul de priorité des incidents (Molka)<br>`P3` Cartes thermiques par quartier (Youssef)<br>`P3` Maintenance prédictive (Molka)<br>`P3` Notifications citoyens (Amen)<br>`P3` Tableau de bord décideurs (Zeineb) | `P1` CRUD complets des 6 services (tous)<br>`P1` Tests unitaires et d'intégration (tous)<br>`P1` Docker Compose + gateway (Amen)<br>`P2` CI GitHub Actions : build + tests<br>`P2` Lien du dépôt Git dans la présentation<br>`P2` Uniformiser CiviAI / CiviIA | `P1` Contrats d'API OpenAPI/Swagger (tous)<br>`P1` Authentification JWT + rôles (Safa)<br>`P1` Bus RabbitMQ entre services (Molka, Amen)<br>`P2` Réservation des ressources (Zeineb) | `P1` README Resource & Logistics : entités + liens (Zeineb)<br>`P1` Cohérence READMEs / diagrammes (tous)<br>`P2` Slide 13 : aligner sur le nouveau module (Zeineb)<br>`P2` Slide 12 : aligner sur le nouveau modèle (Molka)<br>`P2` Lien du diagramme global dans la présentation (Zeineb) | Monorepo + 6 services initialisés<br>Municipal Communication & Events Service (ex-Incident, PR #3)<br>Resource & Logistics Service (squelette)<br>Diagrammes de classes par microservice (6)<br>Diagramme de classes global<br>READMEs des 6 services<br>Présentation séance 4 (24 slides) |

## Détail par service

### Infrastructure Service — Safa
- [x] Squelette du service + README
- [x] Diagramme de classes du service : `Equipment`, `MaintenanceRecord` + 2 énumérations
- [ ] Entités `Equipment`, `MaintenanceRecord`
- [ ] CRUD des équipements et du carnet d'entretien
- [ ] Création du carnet d'entretien à la clôture d'une intervention (`InterventionClosed`)

### Report Service — Youssef
- [x] Squelette du service + README
- [ ] Signalements géolocalisés (`latitude`, `longitude`)
- [ ] Pièces jointes Cloudinary
- [ ] Intégration cartes OpenStreetMap

### Municipal Communication & Events Service — Molka
- [x] Squelette du service + README
- [x] Fusion de la branche `Incident_Service` (PR #3)
- [x] Diagramme de classes du service : `Category`, `Announcement`, `Attachment`, `Event`, `Venue`, `EventRegistration` + 3 énumérations
- [ ] Entités JPA et repositories
- [ ] CRUD des annonces, événements et inscriptions
- [ ] Gestion de la capacité et des inscriptions aux événements

### Resource & Logistics Service — Zeineb
- [x] Squelette du service + README
- [x] Diagramme de classes du service : `Team`, `Resource`, `ResourceReservation` + 4 énumérations
- [ ] Création des entités JPA et des repositories
- [ ] CRUD des 3 entités
- [ ] Disponibilité et réservation des ressources (chevauchement refusé)

### Intervention Service — Yessine
- [x] Squelette du service + README
- [ ] Entités `Intervention`, `Assignment`, `Task`, `Resource`, `Evidence`
- [ ] Planification et affectation des agents
- [ ] Preuves avant / après et clôture

### Citizen Engagement Service — Amen
- [x] Squelette du service + README
- [ ] Confirmations riveraines et commentaires
- [ ] Avis et réclamations
- [ ] Publication des événements RabbitMQ

## Définition de « Done »

Une carte ne passe en **Done** que si :

- le code compile et les tests passent ;
- la Pull Request a été relue par un autre membre ;
- le contrat d'API (OpenAPI/Swagger) est à jour ;
- le README du service est mis à jour ;
- aucun secret ni identifiant n'est commité.

## Règles du board

1. Une carte = une issue GitHub (titre explicite, critère d'acceptation).
2. Deux cartes maximum par personne en colonne **En cours**.
3. Toute carte passe par **Review** avant **Done** (revue croisée).
4. Le board est mis à jour au début et à la fin de chaque séance.
