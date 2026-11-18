package com.civia.resourcelogisticsservice.exception;

import java.time.Instant;

/** Corps d'erreur unique pour tous les services (dossier technique § 9.2). */
public record ApiError(Instant timestamp, int status, String error, String message, String path) {
}
