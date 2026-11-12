# User Service

## 1. Overview

The User Service is a backend microservice of the CiviIA platform. It is responsible for managing the identity, accounts, profiles and access rights of citizens and municipal staff.

It provides REST APIs to create, retrieve, update and deactivate user accounts.

## 2. Main Responsibilities

- Manage user accounts and profiles.
- Manage roles and access rights.
- Store user addresses and preferences.
- Provide user information to other microservices.

## 3. Main Entities

### User
Represents a user account registered in the CiviIA platform.

Suggested attributes:
- id
- firstName
- lastName
- email
- passwordHash
- phoneNumber
- status

### Role
Represents a role used to control access to the platform.

Suggested attributes:
- id
- name
- description

### UserAddress
Represents an address associated with a user.

Suggested attributes:
- id
- userId
- address
- city
- postalCode

### UserPreference
Represents the preferences of a user.

Suggested attributes:
- id
- userId
- language
- preferences

## 4. Technologies

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Validation
- MySQL (database)

## 5. API Endpoints

### User
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| POST | `/api/users` | Create user |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Deactivate user |

### Role
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/roles` | Get all roles |
| GET | `/api/roles/{id}` | Get role by ID |
| POST | `/api/roles` | Create role |
| PUT | `/api/roles/{id}` | Update role |
| DELETE | `/api/roles/{id}` | Delete role |

## 6. Project Structure

```
user-service/
├── src/
│   └── main/
│       ├── java/
│       └── resources/
├── pom.xml
└── README.md
```
