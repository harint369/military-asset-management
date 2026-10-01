package com.militaryasset.service;

import com.militaryasset.dto.PurchaseCreateRequest;
import com.militaryasset.dto.PurchaseItemRequest;
import com.militaryasset.dto.PurchaseItemResponse;
import com.militaryasset.dto.PurchaseResponse;
import com.militaryasset.entity.Base;
import com.militaryasset.entity.EquipmentType;
import com.militaryasset.entity.Purchase;
import com.militaryasset.entity.PurchaseItem;
import com.militaryasset.entity.User;
import com.militaryasset.entity.AuditLog;
import com.militaryasset.exception.BaseNotFoundException;
import com.militaryasset.exception.EquipmentTypeNotFoundException;
import com.militaryasset.exception.PurchaseNotFoundException;
import com.militaryasset.exception.PurchaseValidationException;
import com.militaryasset.exception.UserNotFoundException;
import com.militaryasset.repository.PurchaseRepository;
import com.militaryasset.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.militaryasset.exception.PurchaseApprovalException;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.militaryasset.repository.AuditLogRepository;

import com.militaryasset.repository.BaseRepository;
import com.militaryasset.repository.EquipmentTypeRepository;
import com.militaryasset.repository.InventoryLedgerRepository;
import com.militaryasset.repository.InventoryRepository;
import com.militaryasset.repository.PurchaseItemRepository;
import com.militaryasset.entity.Inventory;
import com.militaryasset.entity.InventoryLedger;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryLedgerRepository inventoryLedgerRepository;
    private final AuditLogRepository auditLogRepository;

    public PurchaseService(
            PurchaseRepository purchaseRepository,
            BaseRepository baseRepository,
            EquipmentTypeRepository equipmentTypeRepository,
            PurchaseItemRepository purchaseItemRepository,
            UserRepository userRepository, 
            InventoryRepository inventoryRepository,
            InventoryLedgerRepository inventoryLedgerRepository,
            AuditLogRepository auditLogRepository) {

        this.purchaseRepository = purchaseRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.purchaseItemRepository = purchaseItemRepository;
        this.userRepository = userRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryLedgerRepository = inventoryLedgerRepository;
        this.auditLogRepository = auditLogRepository;
    }
    
    public PurchaseResponse getPurchaseById(Long id) {

        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() ->
                        new PurchaseNotFoundException(
                                "Purchase not found with id: " + id
                        )
                );

        return mapToResponse(purchase);
    }
    
    public List<PurchaseResponse> getAllPurchases() {

        List<Purchase> purchases = purchaseRepository.findAll();

        if (purchases.isEmpty()) {
            return List.of();
        }

        List<Long> purchaseIds = purchases.stream()
                .map(Purchase::getId)
                .toList();

        List<PurchaseItem> items =
                purchaseItemRepository.findByPurchaseIdIn(purchaseIds);

        Map<Long, List<PurchaseItem>> itemsByPurchase =
                items.stream()
                        .collect(Collectors.groupingBy(
                                item -> item.getPurchase().getId()
                        ));

        return purchases.stream()
                .map(purchase ->
                        mapToResponse(
                                purchase,
                                itemsByPurchase.getOrDefault(
                                        purchase.getId(),
                                        List.of()
                                )
                        )
                )
                .toList();
    }
    
    
    private PurchaseResponse mapToResponse(Purchase purchase) {

        PurchaseResponse response = new PurchaseResponse();

        response.setId(purchase.getId());

        response.setBaseId(purchase.getBase().getId());
        response.setBaseName(purchase.getBase().getName());

        response.setSource(purchase.getSource());
        response.setPurchaseDate(purchase.getPurchaseDate());
        response.setStatus(purchase.getStatus());

        response.setCreatedBy(purchase.getCreatedBy().getId());
        response.setCreatedByName(purchase.getCreatedBy().getFullName());

        if (purchase.getApprovedBy() != null) {
            response.setApprovedBy(purchase.getApprovedBy().getId());
            response.setApprovedByName(
                    purchase.getApprovedBy().getFullName()
            );
        }

        response.setApprovedAt(purchase.getApprovedAt());
        response.setCreatedAt(purchase.getCreatedAt());
        
        List<PurchaseItemResponse> items =
                purchaseItemRepository
                        .findByPurchaseId(purchase.getId())
                        .stream()
                        .map(this::mapItemToResponse)
                        .toList();

        response.setItems(items);

        return response;
    }
    
    private PurchaseResponse mapToResponse(
            Purchase purchase,
            List<PurchaseItem> items) {

        PurchaseResponse response = new PurchaseResponse();

        response.setId(purchase.getId());

        response.setBaseId(purchase.getBase().getId());
        response.setBaseName(purchase.getBase().getName());

        response.setSource(purchase.getSource());
        response.setPurchaseDate(purchase.getPurchaseDate());
        response.setStatus(purchase.getStatus());

        response.setCreatedBy(purchase.getCreatedBy().getId());
        response.setCreatedByName(
                purchase.getCreatedBy().getFullName()
        );

        if (purchase.getApprovedBy() != null) {
            response.setApprovedBy(
                    purchase.getApprovedBy().getId()
            );

            response.setApprovedByName(
                    purchase.getApprovedBy().getFullName()
            );
        }

        response.setApprovedAt(purchase.getApprovedAt());
        response.setCreatedAt(purchase.getCreatedAt());

        List<PurchaseItemResponse> itemResponses =
                items.stream()
                        .map(this::mapItemToResponse)
                        .toList();

        response.setItems(itemResponses);

        return response;
    }
    
    private PurchaseItemResponse mapItemToResponse(
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
    
    @Transactional
    public PurchaseResponse createPurchase(
            PurchaseCreateRequest request,
            Long createdByUserId) {

        Base base = baseRepository.findById(request.getBaseId())
                .orElseThrow(() ->
                        new BaseNotFoundException(
                                "Base not found with id: "
                                        + request.getBaseId()
                        )
                );
        
        if (request.getPurchaseDate().isAfter(LocalDate.now())) {
            throw new PurchaseValidationException(
                    "Purchase date cannot be in the future."
            );
        }
        
        if (!"ACTIVE".equals(base.getStatus())) {
            throw new PurchaseValidationException(
                    "Purchase cannot be created for inactive base: "
                            + base.getName()
            );
        }
        
        Set<Long> equipmentTypeIds = new HashSet<>();

        for (PurchaseItemRequest itemRequest : request.getItems()) {

            if (!equipmentTypeIds.add(itemRequest.getEquipmentTypeId())) {
                throw new PurchaseValidationException(
                        "Duplicate equipment type in purchase: "
                                + itemRequest.getEquipmentTypeId()
                );
            }
        }

        if (createdByUserId == null || createdByUserId <= 0) {
            throw new PurchaseValidationException(
                    "Created by user ID must be greater than zero.");
        }

        User createdBy = userRepository.findById(createdByUserId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + createdByUserId));

        if (!"ACTIVE".equals(createdBy.getStatus())) {
            throw new PurchaseValidationException(
                    "Inactive user cannot create a purchase: "
                            + createdBy.getUsername());
        }
        if (createdBy.getRole() == null) {
            throw new PurchaseValidationException("User has no assigned role");
        }
        if (!"ADMIN".equals(createdBy.getRole().getName())
                && (createdBy.getBase() == null
                    || !createdBy.getBase().getId().equals(base.getId()))) {
            throw new PurchaseValidationException(
                    "User is not authorized for this base");
        }

        Map<Long, EquipmentType> equipmentTypes = new HashMap<>();

        for (PurchaseItemRequest itemRequest : request.getItems()) {

            EquipmentType equipmentType =
                    validatePurchaseItem(itemRequest);

            equipmentTypes.put(
                    itemRequest.getEquipmentTypeId(),
                    equipmentType
            );
        }

        Purchase purchase = new Purchase();

        purchase.setBase(base);
        purchase.setSource(request.getSource());
        purchase.setPurchaseDate(request.getPurchaseDate());
        purchase.setStatus("PENDING");
        purchase.setCreatedBy(createdBy);

        Purchase savedPurchase = purchaseRepository.save(purchase);

        for (PurchaseItemRequest itemRequest : request.getItems()) {

            EquipmentType equipmentType =
                    equipmentTypes.get(
                            itemRequest.getEquipmentTypeId()
                    );

            PurchaseItem item = new PurchaseItem();

            item.setPurchase(savedPurchase);
            item.setEquipmentType(equipmentType);
            item.setQuantity(itemRequest.getQuantity());

            purchaseItemRepository.save(item);
        }

        AuditLog auditLog = new AuditLog();

        auditLog.setUser(createdBy);
        auditLog.setAction("PURCHASE_CREATED");
        auditLog.setEntityType("PURCHASE");
        auditLog.setEntityId(savedPurchase.getId());

        auditLog.setOldValue(null);
        auditLog.setNewValue(
                "{\"status\":\"PENDING\"}"
        );

        auditLog.setTimestamp(LocalDateTime.now());

        
        
        auditLogRepository.save(auditLog);
        
        return mapToResponse(savedPurchase);
    }
    
    @Transactional
    public PurchaseResponse approvePurchase(
            Long purchaseId,
            Long approvedByUserId) {

        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() ->
                        new PurchaseNotFoundException(
                                "Purchase not found with id: "
                                        + purchaseId
                        )
                );

        if (!"PENDING".equals(purchase.getStatus())) {
            throw new PurchaseApprovalException(
                    "Purchase cannot be approved because its current status is: "
                            + purchase.getStatus()
            );
        }

        if (approvedByUserId == null || approvedByUserId <= 0) {
            throw new PurchaseApprovalException(
                    "Approving user ID must be greater than zero");
        }

        User approvedBy = userRepository.findById(approvedByUserId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + approvedByUserId));

        if (!"ACTIVE".equals(approvedBy.getStatus())) {
            throw new PurchaseApprovalException(
                    "Approving user is not active");
        }

        String roleName = approvedBy.getRole() == null
                ? null : approvedBy.getRole().getName();
        if (!"ADMIN".equals(roleName)
                && !"BASE_COMMANDER".equals(roleName)
                && !"LOGISTICS_OFFICER".equals(roleName)) {
            throw new PurchaseApprovalException(
                    "User is not authorized to approve purchases");
        }
        if (!"ADMIN".equals(roleName)
                && (approvedBy.getBase() == null
                    || !approvedBy.getBase().getId().equals(purchase.getBase().getId()))) {
            throw new PurchaseApprovalException(
                    "User is not authorized for this base");
        }

        purchase.setStatus("APPROVED");
        purchase.setApprovedBy(approvedBy);
        purchase.setApprovedAt(LocalDateTime.now());
        
        List<PurchaseItem> purchaseItems =
                purchaseItemRepository.findByPurchaseId(purchaseId);
        
        for (PurchaseItem item : purchaseItems) {

            Inventory inventory = inventoryRepository
                    .findByBaseIdAndEquipmentTypeId(
                            purchase.getBase().getId(),
                            item.getEquipmentType().getId()
                    )
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Inventory not found for base "
                                            + purchase.getBase().getId()
                                            + " and equipment type "
                                            + item.getEquipmentType().getId()
                            )
                    );

            int quantity = item.getQuantity();

            inventory.setTotalQuantity(
                    inventory.getTotalQuantity() + quantity
            );

            inventory.setAvailableQuantity(
                    inventory.getAvailableQuantity() + quantity
            );

            inventoryRepository.save(inventory);
            
            InventoryLedger ledger = new InventoryLedger();

            ledger.setBase(purchase.getBase());
            ledger.setEquipmentType(item.getEquipmentType());
            ledger.setTransactionType("PURCHASE");
            ledger.setTransactionId(purchase.getId());
            ledger.setQuantityIn(quantity);
            ledger.setQuantityOut(0);
            ledger.setBalanceAfter(inventory.getTotalQuantity());
            ledger.setCreatedBy(purchase.getApprovedBy());

            inventoryLedgerRepository.save(ledger);
       
        }
        
        AuditLog auditLog = new AuditLog();

        auditLog.setUser(purchase.getApprovedBy());
        auditLog.setAction("PURCHASE_APPROVED");
        auditLog.setEntityType("PURCHASE");
        auditLog.setEntityId(purchase.getId());
        auditLog.setOldValue("{\"status\":\"PENDING\"}");
        auditLog.setNewValue("{\"status\":\"APPROVED\"}");
        auditLog.setTimestamp(LocalDateTime.now());
        
        auditLogRepository.save(auditLog);

        Purchase savedPurchase = purchaseRepository.save(purchase);

        return mapToResponse(savedPurchase);
    }
    
    @Transactional
    public PurchaseResponse rejectPurchase(
            Long purchaseId,
            Long rejectedByUserId) {

        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() ->
                        new PurchaseNotFoundException(
                                "Purchase not found with id: " + purchaseId
                        )
                );

        if (!"PENDING".equals(purchase.getStatus())) {
            throw new PurchaseApprovalException(
                    "Purchase cannot be rejected because its current status is: "
                            + purchase.getStatus()
            );
        }

        if (rejectedByUserId == null || rejectedByUserId <= 0) {
            throw new PurchaseApprovalException(
                    "Rejecting user ID must be greater than zero");
        }

        User rejectedBy = userRepository.findById(rejectedByUserId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + rejectedByUserId));

        if (!"ACTIVE".equals(rejectedBy.getStatus())) {
            throw new PurchaseApprovalException(
                    "Rejecting user is not active");
        }
        String roleName = rejectedBy.getRole() == null ? null : rejectedBy.getRole().getName();
        if (!"ADMIN".equals(roleName)
                && !"BASE_COMMANDER".equals(roleName)
                && !"LOGISTICS_OFFICER".equals(roleName)) {
            throw new PurchaseApprovalException(
                    "User is not authorized to reject purchases");
        }
        if (!"ADMIN".equals(roleName)
                && (rejectedBy.getBase() == null
                    || !rejectedBy.getBase().getId().equals(purchase.getBase().getId()))) {
            throw new PurchaseApprovalException(
                    "User is not authorized for this base");
        }

        purchase.setStatus("REJECTED");
       

        Purchase savedPurchase = purchaseRepository.save(purchase);

        AuditLog auditLog = new AuditLog();

        auditLog.setUser(rejectedBy);
        auditLog.setAction("PURCHASE_REJECTED");
        auditLog.setEntityType("PURCHASE");
        auditLog.setEntityId(purchase.getId());
        auditLog.setOldValue("{\"status\":\"PENDING\"}");
        auditLog.setNewValue("{\"status\":\"REJECTED\"}");
        auditLog.setTimestamp(LocalDateTime.now());

        auditLogRepository.save(auditLog);

        return mapToResponse(savedPurchase);
    }
    
    private EquipmentType validatePurchaseItem(
            PurchaseItemRequest itemRequest) {

        if (itemRequest.getEquipmentTypeId() == null) {
            throw new PurchaseValidationException(
                    "Equipment type ID is required.");
        }

        if (itemRequest.getQuantity() == null ||
                itemRequest.getQuantity() <= 0) {

            throw new PurchaseValidationException(
                    "Purchase item quantity must be greater than 0.");
        }

        EquipmentType equipmentType =
                equipmentTypeRepository
                        .findById(itemRequest.getEquipmentTypeId())
                        .orElseThrow(() ->
                                new PurchaseValidationException(
                                        "Equipment type not found with id: "
                                        + itemRequest.getEquipmentTypeId()));

        if (!"ACTIVE".equals(equipmentType.getStatus())) {
            throw new PurchaseValidationException(
                    "Equipment type is inactive: "
                    + equipmentType.getName());
        }

        return equipmentType;
    }
    
}
