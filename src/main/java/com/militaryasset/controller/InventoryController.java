package com.militaryasset.controller;

import com.militaryasset.dto.InventoryResponse;
import com.militaryasset.service.InventoryService;


import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public InventoryResponse getInventory(
            @RequestParam Long baseId,
            @RequestParam Long equipmentTypeId) {

        return inventoryService.getInventory(
                baseId,
                equipmentTypeId
        );
    }
    
    @GetMapping("/base/{baseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public List<InventoryResponse> getInventoryByBase(
            @PathVariable Long baseId) {

        return inventoryService.getInventoryByBase(baseId);
    }
    
    @GetMapping("/equipment-type/{equipmentTypeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public List<InventoryResponse> getInventoryByEquipmentType(
            @PathVariable Long equipmentTypeId) {

        return inventoryService.getInventoryByEquipmentType(
                equipmentTypeId
        );
    }
    
}
