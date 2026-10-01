package com.militaryasset.service;

import com.militaryasset.dto.PurchaseItemResponse;
import com.militaryasset.entity.PurchaseItem;
import com.militaryasset.exception.PurchaseItemNotFoundException;
import com.militaryasset.exception.PurchaseNotFoundException;
import com.militaryasset.repository.PurchaseItemRepository;
import com.militaryasset.repository.PurchaseRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class PurchaseItemService {

    private final PurchaseItemRepository purchaseItemRepository;
    private final PurchaseRepository purchaseRepository;
    
    public PurchaseItemService(
            PurchaseItemRepository purchaseItemRepository,
            PurchaseRepository purchaseRepository) {

        this.purchaseItemRepository = purchaseItemRepository;
        this.purchaseRepository = purchaseRepository;
    }
    
    public PurchaseItemResponse getPurchaseItemById(Long id) {

        PurchaseItem item = purchaseItemRepository.findById(id)
                .orElseThrow(() ->
                        new PurchaseItemNotFoundException(
                                "Purchase item not found with id: " + id
                        )
                );

        return mapToResponse(item);
    }
    
    private PurchaseItemResponse mapToResponse(
            PurchaseItem item) {

        PurchaseItemResponse response = new PurchaseItemResponse();

        response.setId(item.getId());

        response.setEquipmentTypeId(
                item.getEquipmentType().getId()
        );

        response.setEquipmentTypeName(
                item.getEquipmentType().getName()
        );

        response.setQuantity(item.getQuantity());

        return response;
    }
    
    public List<PurchaseItemResponse> getItemsByPurchaseId(Long purchaseId) {
    	
	    	if (!purchaseRepository.existsById(purchaseId)) {
	            throw new PurchaseNotFoundException(
	                    "Purchase not found with id: " + purchaseId);
	        }

        List<PurchaseItem> items =
                purchaseItemRepository.findByPurchaseId(purchaseId);

        return items.stream()
                .map(this::mapToResponse)
                .toList();
    }
}
