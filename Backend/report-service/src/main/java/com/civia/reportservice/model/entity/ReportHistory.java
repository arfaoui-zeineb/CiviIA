package com.civia.reportservice.model.entity;

import com.civia.reportservice.model.enums.ReportStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;
import java.util.Objects;

/**
 * Read-only entity representing status history of a report.
 * Rows are populated solely by the PostgreSQL database trigger (trg_report_history).
 */
@Entity
@Table(name = "report_history")
@Immutable
@Getter
@NoArgsConstructor
public class ReportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", insertable = false, updatable = false, nullable = false)
    private Report report;

    @Column(name = "report_id", insertable = false, updatable = false)
    private Long reportId;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 20, insertable = false, updatable = false)
    private ReportStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", length = 20, insertable = false, updatable = false, nullable = false)
    private ReportStatus newStatus;

    @Column(name = "changed_at", insertable = false, updatable = false)
    private Instant changedAt;

    public ReportHistory(Long id, Long reportId, ReportStatus oldStatus, ReportStatus newStatus, Instant changedAt) {
        this.id = id;
        this.reportId = reportId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedAt = changedAt;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ReportHistory that = (ReportHistory) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
