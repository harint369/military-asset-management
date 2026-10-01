package com.militaryasset.controller;

import com.militaryasset.dto.ExpenditureApprovalRequest;
import com.militaryasset.dto.ExpenditureCreateRequest;
import com.militaryasset.dto.ExpenditureResponse;
import com.militaryasset.service.ExpenditureService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expenditures")
public class ExpenditureController {

    private final ExpenditureService expenditureService;

    public ExpenditureController(
            ExpenditureService expenditureService) {

        this.expenditureService = expenditureService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ExpenditureResponse createExpenditure(
            @Valid @RequestBody ExpenditureCreateRequest request) {

        return expenditureService.createExpenditure(request);
    }
    
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ExpenditureResponse approveExpenditure(
            @PathVariable Long id,
            @Valid @RequestBody ExpenditureApprovalRequest request) {

        return expenditureService.approveExpenditure(id, request);
    }
    
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ExpenditureResponse rejectExpenditure(
            @PathVariable Long id,
            @RequestParam Long rejectedByUserId) {

        return expenditureService.rejectExpenditure(
                id, rejectedByUserId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ExpenditureResponse getExpenditureById(
            @PathVariable Long id) {

        return expenditureService.getExpenditureById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public List<ExpenditureResponse> getAllExpenditures() {

        return expenditureService.getAllExpenditures();
    }
}
