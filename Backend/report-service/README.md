# Report Service

## 1. Overview

The Report Service is a backend microservice of the CiviIA platform. It is responsible for recording and managing the urban problems reported by citizens, together with their images and location.

It provides REST APIs to create, retrieve, update and cancel reports.

## 2. Main Responsibilities

- Manage citizen reports.
- Manage report images and categories.
- Store the location of each report.
- Maintain the status history of reports.

## 3. Main Entities

### Report
Represents an urban problem reported by a citizen.

Suggested attributes:
- id
- userId
- title
- description
- categoryId
- latitude
- longitude
- status
- createdAt

### ReportImage
Represents an image attached to a report.

Suggested attributes:
- id
- reportId
- imageUrl
- uploadedAt

### ReportCategory
Represents a category of urban problem.

Suggested attributes:
- id
- name
- description
- active

### ReportHistory
Represents a status change of a report.

Suggested attributes:
- id
- reportId
- oldStatus
- newStatus
- date

## 4. Technologies

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Validation
- MySQL (database)

## 5. API Endpoints

### Report
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/reports` | Get all reports |
| GET | `/api/reports/{id}` | Get report by ID |
| POST | `/api/reports` | Create report |
| PUT | `/api/reports/{id}` | Update report |
| DELETE | `/api/reports/{id}` | Cancel report |

### Report Category
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/report-categories` | Get all report categories |
| GET | `/api/report-categories/{id}` | Get report category by ID |
| POST | `/api/report-categories` | Create report category |
| PUT | `/api/report-categories/{id}` | Update report category |
| DELETE | `/api/report-categories/{id}` | Delete report category |

### Report Image
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/reports/{reportId}/images` | Get images of a report |
| POST | `/api/reports/{reportId}/images` | Add an image to a report |
| DELETE | `/api/report-images/{id}` | Delete a report image |

## 6. Project Structure

```
report-service/
├── src/
│   └── main/
│       ├── java/
│       └── resources/
├── pom.xml
└── README.md
```
