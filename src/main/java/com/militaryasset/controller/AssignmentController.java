package com.militaryasset.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.militaryasset.dto.AssignmentCreateRequest;
import com.militaryasset.dto.AssignmentResponse;
import com.militaryasset.dto.AssignmentReturnRequest;
import com.militaryasset.service.AssignmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public AssignmentResponse createAssignment(
            @Valid @RequestBody AssignmentCreateRequest request) {

        return assignmentService.createAssignment(request);
    }
    
    @PatchMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public AssignmentResponse returnAssignment(
            @PathVariable Long id,
            @Valid @RequestBody AssignmentReturnRequest request) {

        return assignmentService.returnAssignment(id, request);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public AssignmentResponse getAssignmentById(
            @PathVariable Long id) {

        return assignmentService.getAssignmentById(id);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public List<AssignmentResponse> getAllAssignments() {

        return assignmentService.getAllAssignments();
    }
}
