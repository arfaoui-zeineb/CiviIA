package com.civia.municipalcommunicationeventsservice.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.civia.municipalcommunicationeventsservice.domain.model.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

	List<Attachment> findByAnnouncementId(Long announcementId);
}
