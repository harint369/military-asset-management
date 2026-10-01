package com.militaryasset.service;

import com.militaryasset.entity.Inventory;

import com.militaryasset.repository.BaseRepository;
import com.militaryasset.repository.EquipmentTypeRepository;
import com.militaryasset.repository.InventoryRepository;

import java.util.List;

import org.springframework.stereotype.Service;

import com.militaryasset.dto.InventoryResponse;
import com.militaryasset.exception.BaseNotFoundException;
import com.militaryasset.exception.EquipmentTypeNotFoundException;
import com.militaryasset.exception.InventoryNotFoundException;


@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;

    public InventoryService(
            InventoryRepository inventoryRepository,
            BaseRepository baseRepository,
            EquipmentTypeRepository equipmentTypeRepository) {

        this.inventoryRepository = inventoryRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
    }

    public InventoryResponse getInventory(
            Long baseId,
            Long equipmentTypeId) {

       Inventory inventory = inventoryRepository
                .findByBaseIdAndEquipmentTypeId(
                        baseId,
                        equipmentTypeId
                )
                .orElseThrow(() ->
                        new InventoryNotFoundException(
                                "Inventory not found for base "
                                + baseId
                                + " and equipment type "
                                + equipmentTypeId
                        )
                );
       
       return mapToResponse(inventory); 
    }
    
    private InventoryResponse mapToResponse(Inventory inventory) {

    	validateInventoryConsistency(inventory);
    	
        InventoryResponse response = new InventoryResponse();

        response.setId(inventory.getId());

        response.setBaseId(inventory.getBase().getId());
        response.setBaseName(inventory.getBase().getName());

        response.setEquipmentTypeId(
                inventory.getEquipmentType().getId()
        );
        response.setEquipmentTypeName(
                inventory.getEquipmentType().getName()
        );

        response.setTotalQuantity(inventory.getTotalQuantity());
        response.setAvailableQuantity(inventory.getAvailableQuantity());
        response.setAssignedQuantity(inventory.getAssignedQuantity());
        response.setCommittedQuantity(inventory.getCommittedQuantity());
        response.setInTransferQuantity(inventory.getInTransferQuantity());
        response.setRepairQuantity(inventory.getRepairQuantity());

        response.setCreatedAt(inventory.getCreatedAt());
        response.setUpdatedAt(inventory.getUpdatedAt());

        return response;
    }
    
    public List<InventoryResponse> getInventoryByBase(Long baseId) {

        baseRepository.findById(baseId)
                .orElseThrow(() ->
                        new BaseNotFoundException(
                                "Base not found with id: " + baseId
                        )
                );

        return inventoryRepository.findByBaseId(baseId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    public List<InventoryResponse> getInventoryByEquipmentType(
            Long equipmentTypeId) {

        equipmentTypeRepository.findById(equipmentTypeId)
                .orElseThrow(() ->
                        new EquipmentTypeNotFoundException(
                                "Equipment type not found with id: "
                                        + equipmentTypeId
                        )
                );

        return inventoryRepository
                .findByEquipmentTypeId(equipmentTypeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    private void validateInventoryConsistency(Inventory inventory) {

        int calculatedTotal =
                inventory.getAvailableQuantity()
                + inventory.getAssignedQuantity()
                + inventory.getCommittedQuantity()
                + inventory.getInTransferQuantity()
                + inventory.getRepairQuantity();

        if (inventory.getTotalQuantity() != calculatedTotal) {
            throw new IllegalStateException(
                    "Inventory quantity inconsistency detected for inventory id: "
                            + inventory.getId()
            );
        }
    }
    

    
}
