package com.militaryasset.controller;

import com.militaryasset.dto.EquipmentTypeResponse;
import com.militaryasset.service.EquipmentTypeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.militaryasset.dto.EquipmentTypeCreateRequest;
import jakarta.validation.Valid;
import com.militaryasset.dto.EquipmentTypeUpdateRequest;

import java.util.List;

@RestController
@RequestMapping("/api/equipment-types")
public class EquipmentTypeController {

    private final EquipmentTypeService equipmentTypeService;

    public EquipmentTypeController(EquipmentTypeService equipmentTypeService) {
        this.equipmentTypeService = equipmentTypeService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public List<EquipmentTypeResponse> getAllEquipmentTypes() {
        return equipmentTypeService.getAllEquipmentTypes();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public EquipmentTypeResponse getEquipmentTypeById(
            @PathVariable Long id) {

        return equipmentTypeService.getEquipmentTypeById(id);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public EquipmentTypeResponse createEquipmentType(
            @Valid @RequestBody EquipmentTypeCreateRequest request) {

        return equipmentTypeService.createEquipmentType(request);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public EquipmentTypeResponse updateEquipmentType(
            @PathVariable Long id,
            @Valid @RequestBody EquipmentTypeUpdateRequest request) {

        return equipmentTypeService.updateEquipmentType(id, request);
    }
    
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public EquipmentTypeResponse deactivateEquipmentType(
            @PathVariable Long id) {

        return equipmentTypeService.deactivateEquipmentType(id);
    }
}
