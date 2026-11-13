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
| `P2` Classification IA des signalements (Molka)<br>`P2` Détection de doublons GPS + sémantique (Molka)<br>`P2` Recalcul de priorité des incidents (Molka)<br>`P3` Cartes thermiques par quartier (Youssef)<br>`P3` Maintenance prédictive (Molka)<br>`P3` Notifications citoyens (Amen)<br>`P3` Tableau de bord décideurs (Zeineb) | `P1` CRUD complets des 6 services (tous)<br>`P1` Tests unitaires et d'intégration (tous)<br>`P1` Docker Compose + gateway (Amen)<br>`P2` CI GitHub Actions : build + tests<br>`P2` Lien du dépôt Git dans la présentation<br>`P2` Uniformiser CiviAI / CiviIA | `P1` Contrats d'API OpenAPI/Swagger (tous)<br>`P1` Authentification JWT + rôles (Safa)<br>`P1` Bus RabbitMQ : Incident → Municipal → Intervention (Molka, Amen)<br>`P2` Routage catégorie + zone (Zeineb) | `P1` README municipal : entités + liens (Zeineb)<br>`P1` Cohérence READMEs / diagrammes (tous)<br>`P2` Slide 13 : `DepartmentCategory` ajoutée (Zeineb)<br>`P2` Slide 12 : ajouter `IncidentPriority` (Molka)<br>`P2` Lien du diagramme global dans la présentation (Zeineb) | Monorepo + 6 services initialisés<br>Incident Service (PR #3)<br>Municipal Management Service (squelette)<br>Diagrammes de classes par microservice (6)<br>Diagramme de classes global<br>READMEs des 6 services<br>Présentation séance 4 (24 slides) |

## Détail par service

### User Service — Safa
- [x] Squelette du service + README
- [ ] Entités `User`, `Role`
- [ ] Authentification JWT et sécurité Spring Security
- [ ] CRUD utilisateurs et profils

### Report Service — Youssef
- [x] Squelette du service + README
- [ ] Signalements géolocalisés (`latitude`, `longitude`)
- [ ] Pièces jointes Cloudinary
- [ ] Intégration cartes OpenStreetMap

### Incident Service — Molka
- [x] Squelette du service + README
- [x] Fusion de la branche `Incident_Service` (PR #3)
- [ ] Entités `Incident`, `IncidentPriority`
- [ ] Regroupement et détection de doublons (GPS + sémantique)
- [ ] Calcul et recalcul de priorité

### Municipal Management Service — Zeineb
- [x] Squelette du service + README
- [x] Documentation des 5 entités : `Municipality`, `Department`, `Employee`,
      `DepartmentCategory`, `DepartmentZone`
- [x] Diagramme de classes du service
- [ ] Création des entités JPA et des repositories
- [ ] CRUD des 5 entités
- [ ] Routage catégorie + zone vers le département compétent

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
