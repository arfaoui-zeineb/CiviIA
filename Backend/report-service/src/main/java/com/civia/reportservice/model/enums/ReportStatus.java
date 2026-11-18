package com.civia.reportservice.model.enums;

public enum ReportStatus {
    SUBMITTED,
    VALIDATED,
    IN_PROGRESS,
    RESOLVED,
    REJECTED,
    CLOSED;

    /**
     * Checks if transitioning from this status to targetStatus is allowed by business rules.
     * Allowed transitions:
     * - SUBMITTED -> VALIDATED
     * - SUBMITTED -> REJECTED
     * - VALIDATED -> IN_PROGRESS
     * - VALIDATED -> REJECTED
     * - IN_PROGRESS -> RESOLVED
     * - RESOLVED -> CLOSED
     */
    public boolean canTransitionTo(ReportStatus target) {
        if (target == null) {
            return false;
        }
        return switch (this) {
            case SUBMITTED -> target == VALIDATED || target == REJECTED;
            case VALIDATED -> target == IN_PROGRESS || target == REJECTED;
            case IN_PROGRESS -> target == RESOLVED;
            case RESOLVED -> target == CLOSED;
            case REJECTED, CLOSED -> false;
        };
    }
}
