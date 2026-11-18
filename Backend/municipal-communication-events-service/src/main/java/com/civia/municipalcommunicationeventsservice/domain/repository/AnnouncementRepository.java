package com.civia.municipalcommunicationeventsservice.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.civia.municipalcommunicationeventsservice.domain.model.Announcement;
import com.civia.municipalcommunicationeventsservice.domain.model.AnnouncementStatus;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

	List<Announcement> findByStatusAndExpiresAtBefore(AnnouncementStatus status, LocalDateTime dateTime);
}
