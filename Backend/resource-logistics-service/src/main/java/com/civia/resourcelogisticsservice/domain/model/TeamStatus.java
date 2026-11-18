package com.civia.resourcelogisticsservice.domain.model;

/**
 * Statut d'une equipe. Valeurs proposees (le dossier technique indique seulement
 * que le statut doit autoriser la disponibilite de l'equipe).
 */
public enum TeamStatus {
	AVAILABLE,
	UNAVAILABLE,
	MAINTENANCE
}
