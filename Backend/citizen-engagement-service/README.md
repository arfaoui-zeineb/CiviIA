# Citizen Engagement Service

## 1. Overview

The Citizen Engagement Service is a backend microservice of the CiviIA platform. It is responsible for giving citizens the means to confirm problems, follow their evolution, discuss them and evaluate interventions.

It provides REST APIs to create, retrieve, update and delete confirmations, comments, subscriptions, feedbacks and complaints.

## 2. Main Responsibilities

- Manage confirmations of incidents by citizens.
- Manage comments and subscriptions related to incidents.
- Manage feedback on interventions.
- Manage complaints filed by citizens.

## 3. Main Entities

### Confirmation
Represents a citizen confirming that they are concerned by an incident.

Suggested attributes:
- id
- userId
- incidentId
- date

### Comment
Represents a comment posted by a citizen about an incident.

Suggested attributes:
- id
- userId
- incidentId
- content
- createdAt

### Subscription
Represents a citizen subscribing to an incident.

Suggested attributes:
- id
- userId
- incidentId
- createdAt

### Feedback
Represents a citizen evaluating an intervention.

Suggested attributes:
- id
- userId
- interventionId
- rating
- comment

### Complaint
Represents a complaint filed by a citizen about an intervention.

Suggested attributes:
- id
- userId
- interventionId
- reason
- status

## 4. Technologies

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Validation
- MySQL (database)

## 5. API Endpoints

### Confirmation
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/confirmations` | Get all confirmations |
| GET | `/api/confirmations/{id}` | Get confirmation by ID |
| POST | `/api/confirmations` | Create confirmation |
| DELETE | `/api/confirmations/{id}` | Remove confirmation |

### Comment
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/comments` | Get all comments |
| GET | `/api/comments/{id}` | Get comment by ID |
| POST | `/api/comments` | Create comment |
| PUT | `/api/comments/{id}` | Update comment |
| DELETE | `/api/comments/{id}` | Delete comment |

### Subscription
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/subscriptions` | Get all subscriptions |
| GET | `/api/subscriptions/{id}` | Get subscription by ID |
| POST | `/api/subscriptions` | Create subscription |
| DELETE | `/api/subscriptions/{id}` | Delete subscription |

### Feedback
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/feedbacks` | Get all feedbacks |
| GET | `/api/feedbacks/{id}` | Get feedback by ID |
| POST | `/api/feedbacks` | Create feedback |
| PUT | `/api/feedbacks/{id}` | Update feedback |
| DELETE | `/api/feedbacks/{id}` | Delete feedback |

### Complaint
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/complaints` | Get all complaints |
| GET | `/api/complaints/{id}` | Get complaint by ID |
| POST | `/api/complaints` | Create complaint |
| PUT | `/api/complaints/{id}` | Update complaint |
| DELETE | `/api/complaints/{id}` | Delete complaint |

## 6. Project Structure

```
citizen-engagement-service/
├── src/
│   └── main/
│       ├── java/
│       └── resources/
├── pom.xml
└── README.md
```
