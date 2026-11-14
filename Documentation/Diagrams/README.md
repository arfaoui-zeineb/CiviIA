# Diagrammes de classes — CiviIA

Diagrammes de classes des 6 microservices de la plateforme **CiviIA**, établis à partir de la
spécification `CivicPulse — Architecture finale`.

Les six diagrammes par microservice partagent le même thème :

- un en-tête pastel par microservice ;
- les relations UML : composition (◆), agrégation (◇), association (→) ;
- les attributs en **rouge** sont des références à une entité d'un autre service, par
  identifiant, sans clé étrangère inter-base.

## Vue globale

Diagramme de classes global de la plateforme, couvrant les 6 microservices et le paquet `external` (identité fournie par Keycloak).

![Diagramme de classes global](class-diagram-global.png)

## Diagrammes par microservice

| Microservice | Entités | Diagramme |
|---|---|---|
| Infrastructure Service | 2 | [PNG](class-diagram-infrastructure-service.png) · [SVG](class-diagram-infrastructure-service.svg) |
| Report Service | 4 | [PNG](class-diagram-report-service.png) · [SVG](class-diagram-report-service.svg) |
| Municipal Communication & Events Service | 6 | [PNG](class-diagram-municipal-communication-events-service.png) · [SVG](class-diagram-municipal-communication-events-service.svg) |
| Resource & Logistics Service | 3 | [PNG](class-diagram-resource-logistics-service.png) · [SVG](class-diagram-resource-logistics-service.svg) |
| Intervention Service | 6 | [PNG](class-diagram-intervention-service.png) · [SVG](class-diagram-intervention-service.svg) |
| Citizen Engagement Service | 5 | [PNG](class-diagram-citizen-engagement-service.png) · [SVG](class-diagram-citizen-engagement-service.svg) |

Le service Intervention comporte 6 classes : les 5 entités du PDF, plus `Resource`
(référencée par `InterventionResource.resourceId`).

## 1. Infrastructure Service

Domaine : parc d'équipements urbains et carnet d'entretien.

![Infrastructure Service](class-diagram-infrastructure-service.png)

## 2. Report Service

Domaine : signalements, photos et catégories.

![Report Service](class-diagram-report-service.png)

## 3. Municipal Communication & Events Service

Domaine : annonces officielles, événements et inscriptions.

![Municipal Communication & Events Service](class-diagram-municipal-communication-events-service.png)

## 4. Resource & Logistics Service

Domaine : équipes, ressources matérielles et réservations.

![Resource & Logistics Service](class-diagram-resource-logistics-service.png)

## 5. Intervention Service

Domaine : planification, affectations et travaux.

![Intervention Service](class-diagram-intervention-service.png)

## 6. Citizen Engagement Service

Domaine : confirmations, commentaires et évaluations.

![Citizen Engagement Service](class-diagram-citizen-engagement-service.png)
