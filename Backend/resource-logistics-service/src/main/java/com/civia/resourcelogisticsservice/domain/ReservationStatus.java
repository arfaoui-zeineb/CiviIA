package com.civia.resourcelogisticsservice.domain;

import java.util.List;

public enum ReservationStatus {
    PENDING, CONFIRMED, CANCELLED, COMPLETED;

    /** Statuses that block a time slot on a resource or a team. */
    public static final List<ReservationStatus> ACTIVE = List.of(PENDING, CONFIRMED);
}
