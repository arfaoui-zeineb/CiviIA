# Municipal Management Service

## 1. Overview

The Municipal Management Service is a backend microservice of the CiviIA platform. It is responsible for managing municipalities and their administrative departments.

It provides REST APIs to create, retrieve, update and delete municipal information.

## 2. Main Responsibilities

- Manage municipalities.
- Manage departments within each municipality.
- Assign problem categories to departments.
- Maintain relationships between municipalities and departments.
- Provide municipal information to other microservices.

## 3. Main Entities

### Municipality
Represents a municipality registered in the CiviIA platform.

Suggested attributes:
- id
- name
- address
- region
- description
- contactEmail
- phoneNumber

### Department
Represents an administrative department belonging to a municipality.

Suggested attributes:
- id
- name
- description
- municipalityId
- managerId

### DepartmentCategory
Represents the link between a department and a problem category it is responsible for.

Suggested attributes:
- id
- departmentId
- categoryId

## 4. Technologies

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Validation
- MySQL (database)

## 5. API Endpoints

### Municipality
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/municipalities` | Get all municipalities |
| GET | `/api/municipalities/{id}` | Get municipality by ID |
| POST | `/api/municipalities` | Create municipality |
| PUT | `/api/municipalities/{id}` | Update municipality |
| DELETE | `/api/municipalities/{id}` | Delete municipality |

### Department
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/departments` | Get all departments |
| GET | `/api/departments/{id}` | Get department by ID |
| POST | `/api/departments` | Create department |
| PUT | `/api/departments/{id}` | Update department |
| DELETE | `/api/departments/{id}` | Delete department |

### Department Category
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/departments/{id}/categories` | Get categories assigned to a department |
| POST | `/api/departments/{id}/categories` | Assign a category to a department |
| DELETE | `/api/departments/{id}/categories/{categoryId}` | Remove a category from a department |

## 6. Project Structure

```
municipal-management-service/
├── src/
│   └── main/
│       ├── java/
│       └── resources/
├── pom.xml
└── README.md
```

## 7. Class Diagram

![Class diagram](../../Documentation/Diagrams/class-diagram-municipal-management.png)

Full resolution: [class-diagram-municipal-management.svg](../../Documentation/Diagrams/class-diagram-municipal-management.svg)
