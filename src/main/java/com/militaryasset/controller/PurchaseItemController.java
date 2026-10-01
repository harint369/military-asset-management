package com.militaryasset.controller;

import com.militaryasset.dto.PurchaseItemResponse;
import com.militaryasset.service.PurchaseItemService;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase-items")
public class PurchaseItemController {

    private final PurchaseItemService purchaseItemService;
    

    public PurchaseItemController(
            PurchaseItemService purchaseItemService) {

        this.purchaseItemService = purchaseItemService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public PurchaseItemResponse getPurchaseItemById(
            @PathVariable Long id) {

        return purchaseItemService.getPurchaseItemById(id);
    }
    
    @GetMapping("/purchase/{purchaseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public List<PurchaseItemResponse> getItemsByPurchaseId(
            @PathVariable Long purchaseId) {

        return purchaseItemService.getItemsByPurchaseId(purchaseId);
    }
    
    
}
