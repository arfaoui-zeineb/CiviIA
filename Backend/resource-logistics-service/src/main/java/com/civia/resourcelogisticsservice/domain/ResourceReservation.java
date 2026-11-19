package com.civia.resourcelogisticsservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "resource_reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** External identifier: entity owned by this service, no inter-service foreign key. */
    @Column(nullable = false)
    private Long resourceId;

    /** Optional external identifier: entity owned by this service. */
    private Long teamId;

    /** External identifier: Intervention Service, checked by REST at reservation time. */
    @Column(nullable = false)
    private Long interventionId;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status = ReservationStatus.PENDING;

    public void reserve() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }

    public boolean isActive() {
        return ReservationStatus.ACTIVE.contains(status);
    }

    /** [start, end) overlaps [other.start, other.end). */
    public boolean overlaps(ResourceReservation other) {
        return overlaps(other.getStartDateTime(), other.getEndDateTime());
    }

    public boolean overlaps(LocalDateTime start, LocalDateTime end) {
        return startDateTime.isBefore(end) && start.isBefore(endDateTime);
    }
}
