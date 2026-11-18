package com.civicpulse.communication.service.impl;

import com.civicpulse.communication.domain.entity.Announcement;
import com.civicpulse.communication.domain.entity.Attachment;
import com.civicpulse.communication.domain.entity.Category;
import com.civicpulse.communication.domain.entity.Event;
import com.civicpulse.communication.domain.enums.AnnouncementPriority;
import com.civicpulse.communication.domain.enums.AnnouncementStatus;
import com.civicpulse.communication.dto.request.AnnouncementRequest;
import com.civicpulse.communication.dto.response.AnnouncementResponse;
import com.civicpulse.communication.dto.response.AttachmentResponse;
import com.civicpulse.communication.exception.BusinessRuleException;
import com.civicpulse.communication.exception.ResourceNotFoundException;
import com.civicpulse.communication.integration.DepartmentClient;
import com.civicpulse.communication.mapper.AnnouncementMapper;
import com.civicpulse.communication.mapper.AttachmentMapper;
import com.civicpulse.communication.repository.AnnouncementRepository;
import com.civicpulse.communication.repository.AttachmentRepository;
import com.civicpulse.communication.repository.CategoryRepository;
import com.civicpulse.communication.repository.EventRepository;
import com.civicpulse.communication.service.AnnouncementService;
import com.civicpulse.communication.service.StorageService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final AttachmentRepository attachmentRepository;
    private final StorageService storageService;
    private final DepartmentClient departmentClient;
    private final AnnouncementMapper announcementMapper;
    private final AttachmentMapper attachmentMapper;

    @Override
    @Transactional
    public AnnouncementResponse createAnnouncement(AnnouncementRequest request) {
        // Rule 7: Category must be active
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
        if (!category.isActive()) {
            throw new BusinessRuleException("Cannot create announcement with an inactive category", "INACTIVE_CATEGORY");
        }

        // Department validation
        if (!departmentClient.validateDepartmentExists(request.getDepartmentId())) {
            throw new BusinessRuleException("Department with id " + request.getDepartmentId() + " does not exist", "INVALID_DEPARTMENT");
        }

        Event event = null;
        if (request.getEventId() != null) {
            event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event", request.getEventId()));
        }

        Announcement announcement = announcementMapper.toEntity(request);
        announcement.setCategory(category);
        announcement.setEvent(event);
        announcement.setStatus(AnnouncementStatus.DRAFT);
        announcement.setPriority(request.getPriority() != null ? request.getPriority() : AnnouncementPriority.NORMAL);

        Announcement saved = announcementRepository.save(announcement);
        return announcementMapper.toResponse(saved);
    }

    @Override
    public AnnouncementResponse getAnnouncementById(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", id));
        return announcementMapper.toResponse(announcement);
    }

    @Override
    public Page<AnnouncementResponse> getAllAnnouncements(Long categoryId, Long departmentId,
                                                          AnnouncementStatus status, AnnouncementPriority priority,
                                                          Pageable pageable) {
        Specification<Announcement> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (departmentId != null) {
                predicates.add(cb.equal(root.get("departmentId"), departmentId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return announcementRepository.findAll(spec, pageable).map(announcementMapper::toResponse);
    }

    @Override
    @Transactional
    public AnnouncementResponse updateAnnouncement(Long id, AnnouncementRequest request) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", id));

        // Category check
        if (!announcement.getCategory().getId().equals(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
            if (!category.isActive()) {
                throw new BusinessRuleException("Cannot associate an inactive category", "INACTIVE_CATEGORY");
            }
            announcement.setCategory(category);
        }

        // Department check
        if (!announcement.getDepartmentId().equals(request.getDepartmentId())) {
            if (!departmentClient.validateDepartmentExists(request.getDepartmentId())) {
                throw new BusinessRuleException("Department does not exist", "INVALID_DEPARTMENT");
            }
            announcement.setDepartmentId(request.getDepartmentId());
        }

        // Event check
        if (request.getEventId() != null) {
            Event event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event", request.getEventId()));
            announcement.setEvent(event);
        } else {
            announcement.setEvent(null);
        }

        announcementMapper.updateEntityFromRequest(request, announcement);
        return announcementMapper.toResponse(announcement);
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long id) {
        if (!announcementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Announcement", id);
        }
        announcementRepository.deleteById(id);
    }

    // --- State Transitions ---

    @Override
    @Transactional
    public AnnouncementResponse publishAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", id));

        // Rule 6: Transitions par actions dédiées (DRAFT -> PUBLISHED, renseigne publishedAt)
        if (announcement.getStatus() != AnnouncementStatus.DRAFT) {
            throw new BusinessRuleException(
                    "Only DRAFT announcements can be published. Current status: " + announcement.getStatus(),
                    "INVALID_STATUS_TRANSITION");
        }

        announcement.setStatus(AnnouncementStatus.PUBLISHED);
        announcement.setPublishedAt(OffsetDateTime.now());
        return announcementMapper.toResponse(announcement);
    }

    @Override
    @Transactional
    public AnnouncementResponse archiveAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", id));

        // Allowed to archive from PUBLISHED or EXPIRED
        if (announcement.getStatus() != AnnouncementStatus.PUBLISHED && announcement.getStatus() != AnnouncementStatus.EXPIRED) {
            throw new BusinessRuleException(
                    "Cannot archive an announcement with status: " + announcement.getStatus(),
                    "INVALID_STATUS_TRANSITION");
        }

        announcement.setStatus(AnnouncementStatus.ARCHIVED);
        return announcementMapper.toResponse(announcement);
    }

    // --- Attachments ---

    @Override
    @Transactional
    public AttachmentResponse addAttachment(Long announcementId, MultipartFile file) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", announcementId));

        if (file.isEmpty()) {
            throw new BusinessRuleException("Uploaded file cannot be empty", "EMPTY_FILE");
        }

        String fileUrl = storageService.uploadFile(file);

        Attachment attachment = Attachment.builder()
                .announcement(announcement)
                .fileName(file.getOriginalFilename())
                .fileUrl(fileUrl)
                .type(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                .sizeBytes(file.getSize())
                .uploadedAt(OffsetDateTime.now())
                .build();

        announcement.addAttachment(attachment);
        Attachment saved = attachmentRepository.save(attachment);
        return attachmentMapper.toResponse(saved);
    }

    @Override
    public List<AttachmentResponse> getAttachments(Long announcementId) {
        if (!announcementRepository.existsById(announcementId)) {
            throw new ResourceNotFoundException("Announcement", announcementId);
        }
        return attachmentRepository.findByAnnouncementId(announcementId)
                .stream()
                .map(attachmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteAttachment(Long announcementId, Long attachmentId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", announcementId));

        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", attachmentId));

        if (!attachment.getAnnouncement().getId().equals(announcement.getId())) {
            throw new BusinessRuleException("Attachment does not belong to the given announcement", "ATTACHMENT_MISMATCH");
        }

        announcement.removeAttachment(attachment);
        attachmentRepository.delete(attachment);
        storageService.deleteFile(attachment.getFileUrl());
    }

    @Override
    @Transactional
    public AnnouncementResponse createDraftFromAlert(String alertId, String title, String content, Long departmentId, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseGet(() -> categoryRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new BusinessRuleException("No category available for alert announcement", "NO_CATEGORY")));

        Announcement announcement = Announcement.builder()
                .title(title)
                .content(content)
                .departmentId(departmentId != null ? departmentId : 1L)
                .category(category)
                .status(AnnouncementStatus.DRAFT)
                .priority(AnnouncementPriority.URGENT)
                .build();

        Announcement saved = announcementRepository.save(announcement);
        log.info("Created draft announcement id={} from alertId={}", saved.getId(), alertId);
        return announcementMapper.toResponse(saved);
    }
}

