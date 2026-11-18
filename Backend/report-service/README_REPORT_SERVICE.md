# Report Service — CivicPulse Platform

Microservice backend Spring Boot dédié à la gestion des signalements citoyens géolocalisés, de leurs catégories, de leurs pièces jointes et de l'historique d'audit de leur cycle de vie au sein de la plateforme **CivicPulse**.

---

## 1. Technologies & Architecture

- **Java** : 17
- **Framework** : Spring Boot 4.x / Spring Framework 7
- **Persistance** : Spring Data JPA / Hibernate 7
- **Base de données** : PostgreSQL (base `ReportingService`)
- **Migration & Schéma** : Flyway (`src/main/resources/db/migration/V1__init_report_schema.sql`)
- **Documentation API** : Springdoc OpenAPI 3 (Swagger UI & OpenAPI v3)
- **Validation** : Jakarta Bean Validation
- **Architecture** : Clean Multi-layer (Controller / Service / Repository / DTO Records / Mapper / Exception)
- **Transactions & Événements** : Transactional Outbox Pattern (`report_outbox_event`)
- **Fuseau horaire** : UTC (`Instant` / ISO 8601)

---

## 2. Variables d'environnement

Toutes les propriétés de connexion et de configuration disposent de valeurs par défaut pour un lancement local sans friction :

| Variable | Description | Valeur par défaut |
|---|---|---|
| `DB_URL` | URL JDBC de la base PostgreSQL | `jdbc:postgresql://localhost:5432/ReportingService` |
| `DB_USER` | Utilisateur de la base de données | `postgres` |
| `DB_PASSWORD` | Mot de passe de la base | `123456` |
| `PORT` | Port d'écoute HTTP du service | `8081` |

---

## 3. Base de données & Migrations

Au démarrage, Flyway applique automatiquement le script `V1__init_report_schema.sql` :

1. `report_category` : Catégories de signalements (nom unique).
2. `report` : Signalements avec coordonnées géographiques validées (`latitude` ∈ [-90; 90], `longitude` ∈ [-180; 180]), statut forcé à `SUBMITTED` à la création, et références logiques interservices (`user_id`, `equipment_id`).
3. `report_image` : Métadonnées des photos rattachées (URLs Cloudinary).
4. `report_history` : **Entité en lecture seule (`@Immutable`)**. Gérée exclusivement par le trigger PostgreSQL `trg_report_status_change` qui enregistre chaque création ou modification de statut.
5. `report_outbox_event` : Événements métier stockés dans la même transaction (`ReportCreated`, `ReportStatusChanged`).

---

## 4. Compilation et Lancement

### Prérequis
- JDK 17 ou supérieur
- PostgreSQL en cours d'exécution sur le port 5432 avec une base nommée `ReportingService` (ou variables d'environnement configurées)

### Création de la base locale PostgreSQL
```bash
psql -U postgres -c "CREATE DATABASE \"ReportingService\";"
```

### Lancement avec Maven
```bash
# Dans le dossier Backend/report-service
mvn clean spring-boot:run
```

Ou en spécifiant des variables d'environnement personnalisées :
```bash
DB_URL="jdbc:postgresql://localhost:5432/ReportingService" DB_USER="mon_user" DB_PASSWORD="mon_password" PORT=8081 mvn spring-boot:run
```

### Exécution des tests
```bash
mvn test
```

---

## 5. Documentation OpenAPI & Swagger UI

Une fois l'application démarrée :
- **Swagger UI** : [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **Spécification OpenAPI JSON** : [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)

---

## 6. Liste des Endpoints REST (`/api/v1`)

### Catégories (`/api/v1/report-categories`)
| Méthode | URI | Description | Codes HTTP |
|---|---|---|---|
| `GET` | `/api/v1/report-categories` | Liste paginée des catégories (`page`, `size`, `sort`) | `200` |
| `GET` | `/api/v1/report-categories/{id}` | Détail d'une catégorie | `200`, `404` |
| `POST` | `/api/v1/report-categories` | Création d'une catégorie (nom unique) | `201` (avec en-tête `Location`), `400`, `409` |
| `PUT` | `/api/v1/report-categories/{id}` | Modification d'une catégorie | `200`, `400`, `404`, `409` |
| `DELETE` | `/api/v1/report-categories/{id}` | Suppression (refusée si rattachée à des signalements) | `204`, `404`, `409` |

### Signalements (`/api/v1/reports`)
| Méthode | URI | Description | Codes HTTP |
|---|---|---|---|
| `POST` | `/api/v1/reports` | Création d'un signalement (status forcé à `SUBMITTED`, outbox `ReportCreated`) | `201` (avec en-tête `Location`), `400`, `404` |
| `GET` | `/api/v1/reports` | Recherche paginée avec filtres (`status`, `categoryId`, `userId`, `equipmentId`, `createdFrom`, `createdTo`, `search`) | `200` |
| `GET` | `/api/v1/reports/{id}` | Détail complet d'un signalement (catégorie et images incluses) | `200`, `404` |
| `PUT` | `/api/v1/reports/{id}` | Remplacement complet (titre, description, coordonnées, catégorie, équipement) | `200`, `400`, `404` |
| `PATCH` | `/api/v1/reports/{id}` | Modification partielle des mêmes champs | `200`, `400`, `404` |
| `DELETE` | `/api/v1/reports/{id}` | Suppression d'un signalement et cascade | `204`, `404` |

### Transitions de Statut (`/api/v1/reports/{id}/...`)
Chaque transition valide met à jour le statut et insère un événement `ReportStatusChanged` dans `report_outbox_event` au sein de la même transaction.

| Action | URI | Transition autorisée | Code si interdit |
|---|---|---|---|
| Valider | `POST /api/v1/reports/{id}/validate` | `SUBMITTED` → `VALIDATED` | `422 Unprocessable Entity` |
| Démarrer | `POST /api/v1/reports/{id}/start` | `VALIDATED` → `IN_PROGRESS` | `422 Unprocessable Entity` |
| Résoudre | `POST /api/v1/reports/{id}/resolve` | `IN_PROGRESS` → `RESOLVED` | `422 Unprocessable Entity` |
| Rejeter | `POST /api/v1/reports/{id}/reject` | `SUBMITTED` ou `VALIDATED` → `REJECTED` | `422 Unprocessable Entity` |
| Clôturer | `POST /api/v1/reports/{id}/close` | `RESOLVED` → `CLOSED` | `422 Unprocessable Entity` |

### Images (`/api/v1/reports/{reportId}/images`)
| Méthode | URI | Description | Codes HTTP |
|---|---|---|---|
| `GET` | `/api/v1/reports/{reportId}/images` | Liste des images associées à un signalement | `200`, `404` |
| `POST` | `/api/v1/reports/{reportId}/images` | Ajout d'une URL d'image Cloudinary | `201` (avec en-tête `Location`), `400`, `404` |
| `DELETE` | `/api/v1/reports/{reportId}/images/{imageId}` | Suppression d'une image | `204`, `404` |

### Historique d'audit (`/api/v1/reports/{reportId}/history`)
| Méthode | URI | Description | Codes HTTP |
|---|---|---|---|
| `GET` | `/api/v1/reports/{reportId}/history` | Historique des changements de statut (trié par `changedAt` croissant, paginé) | `200`, `404` |

---

## 7. Exemples de requêtes `curl`

### 7.1. Gestion des catégories

#### Créer une catégorie
```bash
curl -i -X POST http://localhost:8081/api/v1/report-categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Voirie & Chaussée"
  }'
```
*Réponse attendue : `201 Created` avec en-tête `Location: http://localhost:8081/api/v1/report-categories/1`*

#### Lister les catégories (paginé)
```bash
curl -X GET "http://localhost:8081/api/v1/report-categories?page=0&size=10&sort=name,asc" \
  -H "Accept: application/json"
```

#### Obtenir une catégorie par ID
```bash
curl -X GET http://localhost:8081/api/v1/report-categories/1 \
  -H "Accept: application/json"
```

#### Mettre à jour une catégorie
```bash
curl -X PUT http://localhost:8081/api/v1/report-categories/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Voirie, Chaussée et Trottoirs"
  }'
```

#### Supprimer une catégorie
```bash
curl -i -X DELETE http://localhost:8081/api/v1/report-categories/1
```
*Si des signalements sont rattachés à cette catégorie, renvoie `409 Conflict`.*

---

### 7.2. Gestion des signalements

#### Créer un signalement
```bash
curl -i -X POST http://localhost:8081/api/v1/reports \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Nid-de-poule dangereux avenue Habib Bourguiba",
    "description": "Trou de 15cm de profondeur sur la voie de droite, risque pour les deux-roues.",
    "latitude": 36.8002,
    "longitude": 10.1865,
    "categoryId": 1,
    "userId": 101,
    "equipmentId": null,
    "imageUrls": [
      "https://res.cloudinary.com/civicpulse/image/upload/v1/reports/hole1.jpg"
    ]
  }'
```
*Réponse attendue : `201 Created` avec statut `SUBMITTED` et en-tête `Location: http://localhost:8081/api/v1/reports/1`*

#### Obtenir le détail d'un signalement
```bash
curl -X GET http://localhost:8081/api/v1/reports/1 \
  -H "Accept: application/json"
```

#### Lister et filtrer les signalements
```bash
curl -X GET "http://localhost:8081/api/v1/reports?status=SUBMITTED&categoryId=1&search=Habib&page=0&size=10&sort=createdAt,desc" \
  -H "Accept: application/json"
```

#### Remplacement complet (PUT)
```bash
curl -X PUT http://localhost:8081/api/v1/reports/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Nid-de-poule profond avenue Habib Bourguiba",
    "description": "Signalement mis à jour : le trou s'\''est agrandi après les fortes pluies.",
    "latitude": 36.8002,
    "longitude": 10.1865,
    "categoryId": 1,
    "equipmentId": 45
  }'
```

#### Modification partielle (PATCH)
```bash
curl -X PATCH http://localhost:8081/api/v1/reports/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Nid-de-poule très urgent avenue Bourguiba"
  }'
```

#### Supprimer un signalement
```bash
curl -i -X DELETE http://localhost:8081/api/v1/reports/1
```
*Réponse attendue : `204 No Content`*

---

### 7.3. Cycle de vie et transitions de statut

#### 1. Valider le signalement (`SUBMITTED` → `VALIDATED`)
```bash
curl -i -X POST http://localhost:8081/api/v1/reports/1/validate
```

#### 2. Démarrer l'intervention (`VALIDATED` → `IN_PROGRESS`)
```bash
curl -i -X POST http://localhost:8081/api/v1/reports/1/start
```

#### 3. Résoudre le signalement (`IN_PROGRESS` → `RESOLVED`)
```bash
curl -i -X POST http://localhost:8081/api/v1/reports/1/resolve
```

#### 4. Clôturer le signalement (`RESOLVED` → `CLOSED`)
```bash
curl -i -X POST http://localhost:8081/api/v1/reports/1/close
```

#### Rejeter un signalement (`SUBMITTED` ou `VALIDATED` → `REJECTED`)
```bash
curl -i -X POST http://localhost:8081/api/v1/reports/1/reject
```

#### Exemple de transition illégale (`SUBMITTED` → `RESOLVED`)
```bash
curl -i -X POST http://localhost:8081/api/v1/reports/1/resolve
```
*Réponse attendue : `422 Unprocessable Entity` :*
```json
{
  "timestamp": "2026-11-18T10:45:00Z",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Cannot transition report status from 'SUBMITTED' to 'RESOLVED'. Allowed transitions: SUBMITTED->VALIDATED, SUBMITTED->REJECTED, VALIDATED->IN_PROGRESS, VALIDATED->REJECTED, IN_PROGRESS->RESOLVED, RESOLVED->CLOSED.",
  "path": "/api/v1/reports/1/resolve",
  "details": []
}
```

---

### 7.4. Pièces jointes (Images)

#### Ajouter une image à un signalement
```bash
curl -i -X POST http://localhost:8081/api/v1/reports/1/images \
  -H "Content-Type: application/json" \
  -d '{
    "cloudinaryUrl": "https://res.cloudinary.com/civicpulse/image/upload/v2/reports/hole1_zoom.jpg",
    "takenAt": "2026-11-18T10:30:00Z"
  }'
```
*Réponse attendue : `201 Created` avec en-tête `Location: http://localhost:8081/api/v1/reports/1/images/2`*

#### Lister les images d'un signalement
```bash
curl -X GET http://localhost:8081/api/v1/reports/1/images \
  -H "Accept: application/json"
```

#### Supprimer une image d'un signalement
```bash
curl -i -X DELETE http://localhost:8081/api/v1/reports/1/images/2
```
*Réponse attendue : `204 No Content`*

---

### 7.5. Historique d'audit

#### Consulter l'historique d'un signalement (lecture seule)
```bash
curl -X GET "http://localhost:8081/api/v1/reports/1/history?page=0&size=20" \
  -H "Accept: application/json"
```
*Renvoie la liste ordonnée par `changedAt` croissant des transitions enregistrées par le trigger PostgreSQL.*
