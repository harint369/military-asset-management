package com.militaryasset.controller;

import com.militaryasset.dto.DashboardResponse;
import com.militaryasset.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public DashboardResponse getDashboard(
            @RequestParam Long baseId,
            @RequestParam Long equipmentTypeId,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {
        return dashboardService.getDashboard(baseId, equipmentTypeId, fromDate, toDate);
    }
}
