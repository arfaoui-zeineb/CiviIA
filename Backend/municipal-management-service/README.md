# Municipal Management Service

## 1. Overview

The Municipal Management Service is a backend microservice of the CiviIA platform. It represents the administrative organisation of the municipality and identifies the teams responsible for each category of urban problem and each geographic area.

It provides REST APIs to create, retrieve, update and deactivate municipalities, departments, employees, category assignments and responsibility zones.

## 2. Main Responsibilities

- Manage municipalities and their administrative departments.
- Manage municipal employees and department managers.
- Assign problem categories to the departments responsible for them.
- Define the geographic zones covered by each department.
- Provide department, employee and zone information to other microservices.

## 3. Main Entities

### Municipality
Represents a municipality registered in the CiviIA platform.

Suggested attributes:
- id
- name
- city
- address

### Department
Represents an administrative department belonging to a municipality.

Suggested attributes:
- id
- name
- city
- address

### Department
Represents an administrative department belonging to a municipality.

Suggested attributes:
- id
- municipalityId
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
| DELETE | `/api/departments/{id}` | Deactivate department |

### Employee
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/employees` | Get all employees |
| GET | `/api/employees/{id}` | Get employee by ID |
| POST | `/api/employees` | Create employee |
| PUT | `/api/employees/{id}` | Update employee |
| DELETE | `/api/employees/{id}` | Deactivate employee |

### Department Category
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/department-categories` | Get all category assignments |
| GET | `/api/department-categories/{id}` | Get a category assignment by ID |
| POST | `/api/department-categories` | Assign a category to a department |
| PUT | `/api/department-categories/{id}` | Update a category assignment |
| DELETE | `/api/department-categories/{id}` | Remove a category assignment |

### Department Zone
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/department-zones` | Get all responsibility zones |
| GET | `/api/department-zones/{id}` | Get a responsibility zone by ID |
| POST | `/api/department-zones` | Create a responsibility zone |
| PUT | `/api/department-zones/{id}` | Update a responsibility zone |
| DELETE | `/api/department-zones/{id}` | Delete a responsibility zone |

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

![Class diagram](../../Documentation/Diagrams/class-diagram-municipal-management-service.png
)

Full resolution: [class-diagram-municipal-management.svg](../../Documentation/Diagrams/class-diagram-municipal-management.svg)
