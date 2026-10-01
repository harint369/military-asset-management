package com.militaryasset.controller;

import com.militaryasset.dto.PurchaseResponse;
import com.militaryasset.service.PurchaseItemService;
import com.militaryasset.service.PurchaseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.militaryasset.dto.PurchaseApprovalRequest;
import com.militaryasset.dto.PurchaseCreateRequest;
import com.militaryasset.dto.PurchaseItemResponse;
import com.militaryasset.dto.PurchaseRejectionRequest;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

	private final PurchaseService purchaseService;
	private final PurchaseItemService purchaseItemService;

	public PurchaseController(
	        PurchaseService purchaseService,
	        PurchaseItemService purchaseItemService) {

	    this.purchaseService = purchaseService;
	    this.purchaseItemService = purchaseItemService;
	}

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public List<PurchaseResponse> getAllPurchases() {
        return purchaseService.getAllPurchases();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public PurchaseResponse getPurchaseById(
            @PathVariable Long id) {

        return purchaseService.getPurchaseById(id);
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public PurchaseResponse createPurchase(
            @Valid @RequestBody PurchaseCreateRequest request) {

        return purchaseService.createPurchase(request, request.getCreatedByUserId());
    }
    
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public PurchaseResponse approvePurchase(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseApprovalRequest request) {

        return purchaseService.approvePurchase(
                id,
                request.getApprovedByUserId()
        );
    }
    
    
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public PurchaseResponse rejectPurchase(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseRejectionRequest request) {

        return purchaseService.rejectPurchase(
                id,
                request.getRejectedByUserId()
        );
    }
    
    @GetMapping("/{purchaseId}/items")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public List<PurchaseItemResponse> getPurchaseItems(
            @PathVariable Long purchaseId) {

        return purchaseItemService.getItemsByPurchaseId(purchaseId);
    }
    
}
