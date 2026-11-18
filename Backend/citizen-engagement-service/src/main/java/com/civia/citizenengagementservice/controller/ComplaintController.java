package com.civia.citizenengagementservice.controller;

import com.civia.citizenengagementservice.dto.ComplaintRequest;
import com.civia.citizenengagementservice.dto.ComplaintResponse;
import com.civia.citizenengagementservice.service.ComplaintService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    // Create
    @PostMapping
    public ResponseEntity<ComplaintResponse> createComplaint(
            @RequestBody ComplaintRequest request) {

        return new ResponseEntity<>(
                complaintService.createComplaint(request),
                HttpStatus.CREATED
        );
    }

    // Get all
    @GetMapping
    public ResponseEntity<List<ComplaintResponse>> getAllComplaints() {

        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<ComplaintResponse> getComplaintById(
            @PathVariable Long id) {

        return ResponseEntity.ok(complaintService.getComplaintById(id));
    }

    // Get by Evaluation ID
    @GetMapping("/evaluation/{evaluationId}")
    public ResponseEntity<List<ComplaintResponse>> getComplaintsByEvaluationId(
            @PathVariable Long evaluationId) {

        return ResponseEntity.ok(
                complaintService.getComplaintsByEvaluationId(evaluationId)
        );
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<ComplaintResponse> updateComplaint(
            @PathVariable Long id,
            @RequestBody ComplaintRequest request) {

        return ResponseEntity.ok(
                complaintService.updateComplaint(id, request)
        );
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplaint(
            @PathVariable Long id) {

        complaintService.deleteComplaint(id);

        return ResponseEntity.noContent().build();
    }

}