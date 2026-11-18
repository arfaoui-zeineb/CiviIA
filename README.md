# Resource & Logistics Service

## 1. Présentation

Le **Resource & Logistics Service** est un microservice backend de la plateforme CivicPulse / CiviIA. Il gère la logistique, la planification et l'affectation des équipes et du matériel nécessaires aux interventions.

C'est le **seul propriétaire** de l'entité `Resource` (dossier technique, point de cohérence 8.1) : le service Intervention ne définit pas sa propre copie de cette notion. Les liens avec les autres services se font uniquement par identifiants (`interventionId`, `resourceId`, `teamId`).

## 2. Responsabilités

- Gérer les équipes (`Team`) et leur disponibilité.
- Gérer les ressources matérielles (`Resource`) et leur statut.
- Réserver des ressources et des équipes pour une intervention (`ResourceReservation`).
- Refuser les réservations qui se chevauchent sur une même ressource.
- Libérer les ressources à l'annulation d'une réservation.

## 3. Modèle de données (dossier technique § 4.5)

### Team

| Attribut | Type |
|---|---|
| id | Long |
| name | String |
| specialty | String |
| zone | String |
| status | `TeamStatus` (`AVAILABLE`, `UNAVAILABLE`, `MAINTENANCE` — valeurs proposées) |

Opération : `isAvailable()` — une équipe est disponible si son statut l'autorise **et** si elle n'a pas de réservation active sur le créneau demandé.

### Resource

| Attribut | Type |
|---|---|
| id | Long |
| name | String |
| type | `ResourceType` (`VEHICLE`, `EQUIPMENT`, `TOOL`) |
| status | `ResourceStatus` (`AVAILABLE`, `RESERVED`, `IN_USE`, `MAINTENANCE`) |
| location | String |

Opérations : `isAvailable()`, `changeStatus()`.

### ResourceReservation

| Attribut | Type |
|---|---|
| id | Long |
| interventionId | Long (référence logique vers Intervention) |
| resourceId | Long (**obligatoire**, § 8.3.3) |
| teamId | Long (optionnel, § 8.3.3) |
| startDateTime | LocalDateTime |
| endDateTime | LocalDateTime |
| status | `ReservationStatus` (`PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`) |

Opérations : `reserve()`, `cancel()`, `checkAvailability()`.

**Règles métier :**

- `checkAvailability()` refuse toute réservation dont l'intervalle chevauche une réservation active (`PENDING` ou `CONFIRMED`) de la même ressource. Deux intervalles qui se touchent exactement ne se chevauchent pas.
- `reserve()` n'est possible que si la ressource est disponible ; la réservation passe en `CONFIRMED` et la ressource en `RESERVED`.
- `cancel()` passe la réservation en `CANCELLED` et libère la ressource si aucune autre réservation active ne la retient.

## 4. API

### Équipes — `/api/teams`

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/teams` | Liste des équipes |
| GET | `/api/teams/{id}` | Équipe par identifiant |
| POST | `/api/teams` | Créer une équipe (201 + `Location`) |
| PUT | `/api/teams/{id}` | Remplacer une équipe |
| DELETE | `/api/teams/{id}` | Supprimer une équipe (204) |
| GET | `/api/teams/{id}/availability?startDateTime=...&endDateTime=...` | Disponibilité sur un créneau |

### Ressources — `/api/resources`

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/resources` | Liste des ressources |
| GET | `/api/resources/{id}` | Ressource par identifiant |
| POST | `/api/resources` | Créer une ressource (201 + `Location`) |
| PUT | `/api/resources/{id}` | Remplacer une ressource |
| PATCH | `/api/resources/{id}/status?status=...` | Changer le statut |
| DELETE | `/api/resources/{id}` | Supprimer une ressource (204) |
| GET | `/api/resources/{id}/availability` | Disponibilité de la ressource |

### Réservations — `/api/reservations`

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/reservations` | Liste des réservations (filtre optionnel `?interventionId=`) |
| GET | `/api/reservations/{id}` | Réservation par identifiant |
| POST | `/api/reservations` | Créer/confirmer une réservation |
| PUT | `/api/reservations/{id}` | Remplacer une réservation active |
| DELETE | `/api/reservations/{id}` | Supprimer une réservation et libérer la ressource |
| POST | `/api/reservations/{id}/cancel` | Annuler une réservation |

### Erreurs

Corps d'erreur unique : `{ timestamp, status, error, message, path }`.

| Code | Cas |
|---|---|
| 400 | Validation (champ manquant, intervalle invalide) |
| 404 | Entité introuvable |
| 409 | Conflit de réservation (chevauchement) |
| 422 | Règle métier violée (ressource/équipe indisponible) |

## 5. Technologies

Java 17 · Spring Boot 4.1.2 · Spring Web MVC · Spring Data JPA · Spring Validation · Lombok · MySQL (H2 en tests).

## 6. Structure

```
resource-logistics-service/
├── src/main/java/com/civia/resourcelogisticsservice/
│   ├── ResourceLogisticsServiceApplication.java
│   ├── domain/                # Team, Resource, ResourceReservation + énumérations
│   ├── repository/            # repositories JPA
│   ├── service/               # ITeamService, IResourceService, IResourceReservationService + implémentations
│   ├── controller/            # contrôleurs REST (entités en @RequestBody)
│   └── exception/             # erreurs et gestionnaire global
├── src/main/resources/application.properties
└── src/test/java/             # tests unitaires et test de contexte
```

L'organisation suit celle du projet **Auto-Loc** : entités et énumérations dans `domain`, repositories `@Repository` dans `repository`, contrats `I...Service` et leurs implémentations dans `service`, contrôleurs `@RestController` dans `controller` (injection par `@RequiredArgsConstructor`, entités en `@RequestBody`).

## 7. Diagramme de classes

![Diagramme de classes du Resource & Logistics Service](Documentation/Diagrams/class-diagram-resource-logistics-service.png)

Version haute résolution : [class-diagram-resource-logistics-service.svg](Documentation/Diagrams/class-diagram-resource-logistics-service.svg)

## 8. Lancement

```bash
./mvnw spring-boot:run
```

La base de données peut être configurée via `DB_URL`, `DB_USERNAME` et `DB_PASSWORD`.
