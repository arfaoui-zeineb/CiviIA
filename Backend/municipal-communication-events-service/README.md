# CivicPulse - Microservice Municipal Communication & Events

Service Spring Boot autonome assurant la communication officielle de la municipalité (annonces, avis, alertes) et l'organisation d'événements locaux (inscriptions des citoyens).

---

## 1. Caractéristiques d'Architecture

- **Autonomie des données** : Base relationnelle propre (PostgreSQL), aucun couplage avec d'autres bases de données.
- **Identités externes** : Références logiques via `departmentId` et `userId` (fourni via token JWT / Keycloak claim `sub`).
- **Inscriptions concurrentes sécurisées** : Incrément atomique conditionnel SQL avec basculement automatique du statut en `FULL` et retour en `OPEN` lors des annulations.
- **Contrainte d'unicité partielle** : Index unique `(event_id, user_id) WHERE status = 'REGISTERED'` empêchant les inscriptions multiples actives.
- **Stockage de fichiers** : Découplé via `StorageService` (Cloudinary pour la production, mock pour le développement local).
- **Résilience** : Circuit Breaker et Retry Resilience4j pour les appels d'annuaire des départements avec fonctionnement en mode dégradé.
- **Tâche planifiée** : Expiration automatique périodique des annonces dont la date `expiresAt` est échue.

---

## 2. Démarrage Rapide

### Prérequis
- Java 17+
- Maven 3.8+
- Docker & Docker Compose (optionnel pour PostgreSQL)

### Lancement avec Docker Compose
```bash
docker-compose up -d
```

### Lancement en local (profil dev)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
L'application démarre sur le port `8081`.

---

## 3. Endpoints Principaux

| Ressource | Méthode | URI | Description |
| :--- | :--- | :--- | :--- |
| **Categories** | `POST` | `/api/v1/categories` | Créer une catégorie |
| | `GET` | `/api/v1/categories` | Liste paginée des catégories |
| **Venues** | `POST` | `/api/v1/venues` | Créer un lieu municipal |
| | `GET` | `/api/v1/venues` | Liste paginée des lieux |
| **Events** | `POST` | `/api/v1/events` | Créer un événement (DRAFT) |
| | `GET` | `/api/v1/events` | Liste avec filtres et pagination |
| | `POST` | `/api/v1/events/{id}/open` | Ouvrir l'événement aux inscriptions |
| | `POST` | `/api/v1/events/{id}/close` | Clôturer l'événement |
| | `POST` | `/api/v1/events/{id}/cancel` | Annuler l'événement |
| | `POST` | `/api/v1/events/{id}/registrations` | S'inscrire à l'événement |
| | `POST` | `/api/v1/events/{id}/registrations/cancel`| Annuler son inscription |
| **Announcements** | `POST` | `/api/v1/announcements` | Créer une annonce |
| | `POST` | `/api/v1/announcements/{id}/publish` | Publier l'annonce |
| | `POST` | `/api/v1/announcements/{id}/archive` | Archiver l'annonce |
| | `POST` | `/api/v1/announcements/{id}/attachments`| Téléverser une pièce jointe |

Documentation interactive OpenAPI / Swagger UI :  
`http://localhost:8081/swagger-ui.html`

---

## 4. Tests

Exécuter la suite complète de tests (tests unitaires de règles métier, tests de concurrence multithreadés, tests de tâches planifiées) :
```bash
mvn test
```

