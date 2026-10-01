package com.militaryasset.controller;

import com.militaryasset.dto.RepairCompleteRequest;
import com.militaryasset.dto.RepairRequest;
import com.militaryasset.dto.RepairResponse;
import com.militaryasset.service.RepairService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/repairs")
public class RepairController {

    private final RepairService repairService;

    public RepairController(RepairService repairService) {
        this.repairService = repairService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public RepairResponse startRepair(
            @Valid @RequestBody RepairRequest request) {

        return repairService.startRepair(request);
    }
    
    @PatchMapping("/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public RepairResponse completeRepair(
            @Valid @RequestBody RepairCompleteRequest request) {

        return repairService.completeRepair(request);
    }
}
