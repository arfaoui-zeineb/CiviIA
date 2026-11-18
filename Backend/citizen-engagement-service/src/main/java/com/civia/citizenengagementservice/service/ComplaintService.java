package com.civia.citizenengagementservice.service;

import com.civia.citizenengagementservice.dto.ComplaintRequest;
import com.civia.citizenengagementservice.dto.ComplaintResponse;
import com.civia.citizenengagementservice.entities.Complaint;
import com.civia.citizenengagementservice.enums.ComplaintStatus;
import com.civia.citizenengagementservice.repository.ComplaintRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    // 1. Create
    public ComplaintResponse createComplaint(ComplaintRequest request) {

        Complaint complaint = new Complaint();

        complaint.setEvaluationId(request.getEvaluationId());
        complaint.setUserId(request.getUserId());
        complaint.setReason(request.getReason());
        complaint.setResolution(request.getResolution());
        complaint.setStatus(ComplaintStatus.OPEN);
        complaint.setCreatedAt(LocalDateTime.now());

        Complaint savedComplaint = complaintRepository.save(complaint);

        return mapToResponse(savedComplaint);
    }

    // 2. Get all
    public List<ComplaintResponse> getAllComplaints() {

        return complaintRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 3. Get by ID
    public ComplaintResponse getComplaintById(Long id) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        return mapToResponse(complaint);
    }

    // 4. Get complaints by Evaluation ID
    public List<ComplaintResponse> getComplaintsByEvaluationId(Long evaluationId) {

        return complaintRepository.findByEvaluationId(evaluationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 5. Update
    public ComplaintResponse updateComplaint(Long id, ComplaintRequest request) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setEvaluationId(request.getEvaluationId());
        complaint.setUserId(request.getUserId());
        complaint.setReason(request.getReason());
        complaint.setResolution(request.getResolution());

        Complaint updatedComplaint = complaintRepository.save(complaint);

        return mapToResponse(updatedComplaint);
    }

    // 6. Delete
    public void deleteComplaint(Long id) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaintRepository.delete(complaint);
    }

    // Convert Entity to Response DTO
    private ComplaintResponse mapToResponse(Complaint complaint) {

        return new ComplaintResponse(
                complaint.getId(),
                complaint.getEvaluationId(),
                complaint.getUserId(),
                complaint.getReason(),
                complaint.getStatus(),
                complaint.getResolution(),
                complaint.getCreatedAt()
        );
    }
}