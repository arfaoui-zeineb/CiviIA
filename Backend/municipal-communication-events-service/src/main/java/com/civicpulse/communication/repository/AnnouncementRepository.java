package com.civicpulse.communication.repository;

import com.civicpulse.communication.domain.entity.Announcement;
import com.civicpulse.communication.domain.enums.AnnouncementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long>, JpaSpecificationExecutor<Announcement> {

    @Query("SELECT a FROM Announcement a WHERE a.status = 'PUBLISHED' AND a.expiresAt IS NOT NULL AND a.expiresAt <= :now")
    List<Announcement> findExpiredAnnouncements(@Param("now") OffsetDateTime now);

    @Modifying
    @Query("UPDATE Announcement a SET a.status = :newStatus WHERE a.status = 'PUBLISHED' AND a.expiresAt IS NOT NULL AND a.expiresAt <= :now")
    int markExpiredAnnouncements(@Param("now") OffsetDateTime now, @Param("newStatus") AnnouncementStatus newStatus);
}

