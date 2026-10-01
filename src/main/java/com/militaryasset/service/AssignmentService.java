package com.militaryasset.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.militaryasset.dto.AssignmentCreateRequest;
import com.militaryasset.dto.AssignmentResponse;
import com.militaryasset.dto.PurchaseItemResponse;
import com.militaryasset.dto.PurchaseResponse;
import com.militaryasset.entity.Assignment;
import com.militaryasset.entity.AuditLog;
import com.militaryasset.entity.Base;
import com.militaryasset.entity.EquipmentType;
import com.militaryasset.entity.Inventory;
import com.militaryasset.entity.Purchase;
import com.militaryasset.entity.User;
import com.militaryasset.exception.AssignmentNotFoundException;
import com.militaryasset.exception.AssignmentValidationException;
import com.militaryasset.repository.AssignmentRepository;
import com.militaryasset.repository.AuditLogRepository;
import com.militaryasset.repository.BaseRepository;
import com.militaryasset.repository.EquipmentTypeRepository;
import com.militaryasset.repository.InventoryRepository;
import com.militaryasset.repository.UserRepository;
import com.militaryasset.dto.AssignmentReturnRequest;


@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            BaseRepository baseRepository,
            EquipmentTypeRepository equipmentTypeRepository,
            InventoryRepository inventoryRepository,
            UserRepository userRepository, 
            AuditLogRepository auditLogRepository) {

        this.assignmentRepository = assignmentRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }
    
    @Transactional
    public AssignmentResponse createAssignment(
            AssignmentCreateRequest request) {
    	
    	Base base = baseRepository.findById(request.getBaseId())
    	        .orElseThrow(() ->
    	                new AssignmentValidationException(
    	                        "Base not found with id: "
    	                                + request.getBaseId()));

    	if (!"ACTIVE".equals(base.getStatus())) {
    	    throw new AssignmentValidationException(
    	            "Base is not active: " + base.getName());
    	}
    	
    	EquipmentType equipmentType = equipmentTypeRepository
    	        .findById(request.getEquipmentTypeId())
    	        .orElseThrow(() ->
    	                new AssignmentValidationException(
    	                        "Equipment type not found with id: "
    	                                + request.getEquipmentTypeId()));

    	if (!"ACTIVE".equals(equipmentType.getStatus())) {
    	    throw new AssignmentValidationException(
    	            "Equipment type is not active: "
    	                    + equipmentType.getName());
    	}
    	
    	User creator = userRepository.findById(request.getCreatedByUserId())
    	        .orElseThrow(() ->
    	                new AssignmentValidationException(
    	                        "User not found with id: "
    	                                + request.getCreatedByUserId()));

    	if (!"ACTIVE".equals(creator.getStatus())) {
    	    throw new AssignmentValidationException(
    	            "User is not active: " + creator.getUsername());
    	}
    	
    	if (request.getQuantity() == null || request.getQuantity() <= 0) {
    	    throw new AssignmentValidationException(
    	            "Assignment quantity must be greater than 0");
    	}

    	if (request.getAssignedToType() == null
    	        || request.getAssignedToType().isBlank()) {
    	    throw new AssignmentValidationException(
    	            "Assigned-to type is required");
    	}

    	if (request.getAssignedToId() == null
    	        || request.getAssignedToId() <= 0) {
    	    throw new AssignmentValidationException(
    	            "Assigned-to ID must be greater than 0");
    	}
    	
    	if (creator.getRole() == null) {
    	    throw new AssignmentValidationException(
    	            "User has no assigned role");
    	}

    	if (!"ADMIN".equals(creator.getRole().getName())) {
    	    if (creator.getBase() == null
    	            || !creator.getBase().getId().equals(base.getId())) {
    	        throw new AssignmentValidationException(
    	                "User is not authorized to create assignments for this base");
    	    }
    	}
    	
    	Inventory inventory = inventoryRepository
    	        .findByBaseIdAndEquipmentTypeId(
    	                base.getId(),
    	                equipmentType.getId())
    	        .orElseThrow(() ->
    	                new AssignmentValidationException(
    	                        "Inventory not found for this base and equipment type"));

    	if (inventory.getAvailableQuantity() < request.getQuantity()) {
    	    throw new AssignmentValidationException(
    	            "Insufficient available inventory");
    	}
    	
    	int quantity = request.getQuantity();

    	inventory.setAvailableQuantity(
    	        inventory.getAvailableQuantity() - quantity);

    	inventory.setAssignedQuantity(
    	        inventory.getAssignedQuantity() + quantity);

    	inventoryRepository.save(inventory);

    	Assignment assignment = new Assignment();

    	assignment.setBase(base);
    	assignment.setEquipmentType(equipmentType);
    	assignment.setQuantity(quantity);
    	assignment.setAssignedToType(request.getAssignedToType());
    	assignment.setAssignedToId(request.getAssignedToId());
    	assignment.setStatus("ACTIVE");
    	assignment.setCreatedBy(creator);

    	Assignment savedAssignment = assignmentRepository.save(assignment);

    	AuditLog auditLog = new AuditLog();
    	auditLog.setUser(creator);
    	auditLog.setAction("ASSIGNMENT_CREATED");
    	auditLog.setEntityType("ASSIGNMENT");
    	auditLog.setEntityId(savedAssignment.getId());

    	auditLogRepository.save(auditLog);
    	
    	return mapToResponse(savedAssignment);
    	
    }
    
    private AssignmentResponse mapToResponse(Assignment assignment) {

        AssignmentResponse response = new AssignmentResponse();

        response.setId(assignment.getId());

        response.setBaseId(assignment.getBase().getId());
        response.setBaseName(assignment.getBase().getName());

        response.setEquipmentTypeId(
                assignment.getEquipmentType().getId());
        response.setEquipmentTypeName(
                assignment.getEquipmentType().getName());

        response.setQuantity(assignment.getQuantity());

        response.setAssignedToType(
                assignment.getAssignedToType());
        response.setAssignedToId(
                assignment.getAssignedToId());

        response.setStatus(assignment.getStatus());

        response.setAssignedAt(
                assignment.getAssignedAt());
        response.setReturnedAt(
                assignment.getReturnedAt());

        response.setCreatedBy(
                assignment.getCreatedBy().getId());
        response.setCreatedByName(
                assignment.getCreatedBy().getFullName());

        return response;
    }
    
    @Transactional
    public AssignmentResponse returnAssignment(
            Long assignmentId,
            AssignmentReturnRequest request) {

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() ->
                        new AssignmentNotFoundException(
                                "Assignment not found with id: " + assignmentId));

        if (!"ACTIVE".equals(assignment.getStatus())) {
            throw new AssignmentValidationException(
                    "Assignment is not active and cannot be returned");
        }

        User returnedBy = userRepository.findById(
                request.getReturnedByUserId()
        ).orElseThrow(() ->
                new AssignmentValidationException(
                        "User not found with id: "
                                + request.getReturnedByUserId()));

        if (!"ACTIVE".equals(returnedBy.getStatus())) {
            throw new AssignmentValidationException(
                    "User is not active: " + returnedBy.getUsername());
        }
        if (returnedBy.getRole() == null) {
            throw new AssignmentValidationException("User has no assigned role");
        }
        if (!"ADMIN".equals(returnedBy.getRole().getName())
                && (returnedBy.getBase() == null
                    || !returnedBy.getBase().getId().equals(assignment.getBase().getId()))) {
            throw new AssignmentValidationException(
                    "User is not authorized to return this assignment");
        }

        Inventory inventory = inventoryRepository
                .findByBaseIdAndEquipmentTypeId(
                        assignment.getBase().getId(),
                        assignment.getEquipmentType().getId())
                .orElseThrow(() ->
                        new AssignmentValidationException(
                                "Inventory not found for this assignment"));

        int quantity = assignment.getQuantity();

        if (inventory.getAssignedQuantity() < quantity) {
            throw new AssignmentValidationException(
                    "Insufficient assigned inventory for return");
        }

        inventory.setAssignedQuantity(
                inventory.getAssignedQuantity() - quantity);

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity() + quantity);

        inventoryRepository.save(inventory);

        assignment.setStatus("RETURNED");
        assignment.setReturnedAt(
                java.time.LocalDateTime.now());

        Assignment savedAssignment =
                assignmentRepository.save(assignment);

        AuditLog auditLog = new AuditLog();
        auditLog.setUser(returnedBy);
        auditLog.setAction("ASSIGNMENT_RETURNED");
        auditLog.setEntityType("ASSIGNMENT");
        auditLog.setEntityId(savedAssignment.getId());

        auditLogRepository.save(auditLog);
        
        return mapToResponse(savedAssignment);
    }
    
    public AssignmentResponse getAssignmentById(Long assignmentId) {

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() ->
                        new AssignmentNotFoundException(
                                "Assignment not found with id: " + assignmentId));

        return mapToResponse(assignment);
    }
    
    public List<AssignmentResponse> getAllAssignments() {

        return assignmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
}
