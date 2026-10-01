package com.militaryasset.controller;

import com.militaryasset.entity.Base;
import com.militaryasset.service.BaseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.militaryasset.dto.BaseCreateRequest;
import com.militaryasset.dto.BaseUpdateRequest;
import jakarta.validation.Valid;
import com.militaryasset.dto.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/api/bases")
public class BaseController {

    private final BaseService baseService;

    public BaseController(BaseService baseService) {
        this.baseService = baseService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public List<BaseResponse> getAllBases() {
        return baseService.getAllBases();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public BaseResponse getBaseById(@PathVariable Long id) {
        return baseService.getBaseById(id);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse createBase(@Valid @RequestBody BaseCreateRequest request) {
        return baseService.createBase(request);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse updateBase(
            @PathVariable Long id,
            @Valid @RequestBody BaseUpdateRequest request) {

        return baseService.updateBase(id, request);
    }
    
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse deactivateBase(@PathVariable Long id) {
        return baseService.deactivateBase(id);
    }
}
