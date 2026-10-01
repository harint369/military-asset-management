package com.militaryasset.service;

import com.militaryasset.dto.ExpenditureApprovalRequest;
import com.militaryasset.dto.ExpenditureCreateRequest;
import com.militaryasset.dto.ExpenditureResponse;
import com.militaryasset.entity.Assignment;
import com.militaryasset.entity.AuditLog;
import com.militaryasset.entity.Base;
import com.militaryasset.entity.EquipmentType;
import com.militaryasset.entity.Expenditure;
import com.militaryasset.entity.Inventory;
import com.militaryasset.entity.InventoryLedger;
import com.militaryasset.entity.User;
import com.militaryasset.exception.ExpenditureApprovalException;
import com.militaryasset.exception.ExpenditureNotFoundException;
import com.militaryasset.exception.ExpenditureValidationException;
import com.militaryasset.repository.AssignmentRepository;
import com.militaryasset.repository.AuditLogRepository;
import com.militaryasset.repository.BaseRepository;
import com.militaryasset.repository.EquipmentTypeRepository;
import com.militaryasset.repository.ExpenditureRepository;
import com.militaryasset.repository.InventoryLedgerRepository;
import com.militaryasset.repository.InventoryRepository;
import com.militaryasset.repository.UserRepository;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenditureService {

    private final ExpenditureRepository expenditureRepository;
    private final AssignmentRepository assignmentRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final InventoryLedgerRepository inventoryLedgerRepository;
    private final AuditLogRepository auditLogRepository;

    public ExpenditureService(
            ExpenditureRepository expenditureRepository,
            AssignmentRepository assignmentRepository,
            BaseRepository baseRepository,
            EquipmentTypeRepository equipmentTypeRepository,
            InventoryRepository inventoryRepository,
            UserRepository userRepository,
            InventoryLedgerRepository inventoryLedgerRepository,
            AuditLogRepository auditLogRepository) {

        this.expenditureRepository = expenditureRepository;
        this.assignmentRepository = assignmentRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
        this.inventoryLedgerRepository = inventoryLedgerRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public ExpenditureResponse createExpenditure(
            ExpenditureCreateRequest request) {

        Base base = baseRepository.findById(request.getBaseId())
                .orElseThrow(() ->
                        new ExpenditureValidationException(
                                "Base not found with id: "
                                        + request.getBaseId()));

        if (!"ACTIVE".equals(base.getStatus())) {
            throw new ExpenditureValidationException(
                    "Base is not active: " + base.getName());
        }

        EquipmentType equipmentType =
                equipmentTypeRepository.findById(
                        request.getEquipmentTypeId())
                .orElseThrow(() ->
                        new ExpenditureValidationException(
                                "Equipment type not found with id: "
                                        + request.getEquipmentTypeId()));

        if (!"ACTIVE".equals(equipmentType.getStatus())) {
            throw new ExpenditureValidationException(
                    "Equipment type is not active: "
                            + equipmentType.getName());
        }

        User creator = userRepository.findById(
                request.getCreatedByUserId())
                .orElseThrow(() ->
                        new ExpenditureValidationException(
                                "User not found with id: "
                                        + request.getCreatedByUserId()));

        if (!"ACTIVE".equals(creator.getStatus())) {
            throw new ExpenditureValidationException(
                    "User is not active: " + creator.getUsername());
        }

        if (creator.getRole() == null) {
            throw new ExpenditureValidationException("User has no assigned role");
        }
        if (!"ADMIN".equals(creator.getRole().getName())
                && (creator.getBase() == null || !creator.getBase().getId().equals(base.getId()))) {
            throw new ExpenditureValidationException(
                    "User is not authorized for this base");
        }

        if (request.getQuantity() == null
                || request.getQuantity() <= 0) {

            throw new ExpenditureValidationException(
                    "Expenditure quantity must be greater than 0");
        }

        if (request.getReason() == null
                || request.getReason().isBlank()) {

            throw new ExpenditureValidationException(
                    "Expenditure reason is required");
        }

        if (request.getExpendedAt() == null) {
            throw new ExpenditureValidationException(
                    "Expenditure date is required");
        }

        Inventory inventory =
                inventoryRepository
                        .findByBaseIdAndEquipmentTypeId(
                                base.getId(),
                                equipmentType.getId())
                        .orElseThrow(() ->
                                new ExpenditureValidationException(
                                        "Inventory not found for this base and equipment type"));

        Assignment assignment = null;

    
        if (request.getAssignmentId() != null) {

            assignment = assignmentRepository
                    .findById(request.getAssignmentId())
                    .orElseThrow(() ->
                            new ExpenditureValidationException(
                                    "Assignment not found with id: "
                                            + request.getAssignmentId()));

            if (!"ACTIVE".equals(assignment.getStatus())) {
                throw new ExpenditureValidationException(
                        "Assignment is not active");
            }

            if (!assignment.getBase().getId().equals(base.getId())) {
                throw new ExpenditureValidationException(
                        "Assignment does not belong to the specified base");
            }

            if (!assignment.getEquipmentType()
                    .getId()
                    .equals(equipmentType.getId())) {

                throw new ExpenditureValidationException(
                        "Assignment equipment type does not match expenditure equipment type");
            }

            if (assignment.getQuantity()
                    < request.getQuantity()) {

                throw new ExpenditureValidationException(
                        "Expenditure quantity exceeds assigned quantity");
            }

        } else {

            if (inventory.getAvailableQuantity()
                    < request.getQuantity()) {

                throw new ExpenditureValidationException(
                        "Insufficient available inventory");
            }
        }

        Expenditure expenditure = new Expenditure();

        expenditure.setBase(base);
        expenditure.setEquipmentType(equipmentType);
        expenditure.setAssignment(assignment);
        expenditure.setQuantity(request.getQuantity());
        expenditure.setReason(request.getReason());
        expenditure.setExpendedAt(request.getExpendedAt());
        expenditure.setStatus("PENDING");
        expenditure.setCreatedBy(creator);

        Expenditure savedExpenditure =
                expenditureRepository.save(expenditure);

        return mapToResponse(savedExpenditure);
    }

    private ExpenditureResponse mapToResponse(
            Expenditure expenditure) {

        ExpenditureResponse response =
                new ExpenditureResponse();

        response.setId(expenditure.getId());

        response.setBaseId(
                expenditure.getBase().getId());
        response.setBaseName(
                expenditure.getBase().getName());

        response.setEquipmentTypeId(
                expenditure.getEquipmentType().getId());
        response.setEquipmentTypeName(
                expenditure.getEquipmentType().getName());

        if (expenditure.getAssignment() != null) {
            response.setAssignmentId(
                    expenditure.getAssignment().getId());
        }

        response.setQuantity(
                expenditure.getQuantity());

        response.setReason(
                expenditure.getReason());

        response.setExpendedAt(
                expenditure.getExpendedAt());

        response.setStatus(
                expenditure.getStatus());

        response.setCreatedBy(
                expenditure.getCreatedBy().getId());

        response.setCreatedByName(
                expenditure.getCreatedBy().getFullName());

        if (expenditure.getApprovedBy() != null) {
            response.setApprovedBy(
                    expenditure.getApprovedBy().getId());

            response.setApprovedByName(
                    expenditure.getApprovedBy().getFullName());
        }

        response.setApprovedAt(
                expenditure.getApprovedAt());

        response.setCreatedAt(
                expenditure.getCreatedAt());

        return response;
    }
    
    @Transactional
    public ExpenditureResponse approveExpenditure(
            Long expenditureId,
            ExpenditureApprovalRequest request) {

        Expenditure expenditure = expenditureRepository
                .findById(expenditureId)
                .orElseThrow(() ->
                        new ExpenditureNotFoundException(
                                "Expenditure not found with id: "
                                        + expenditureId));

        if (!"PENDING".equals(expenditure.getStatus())) {
            throw new ExpenditureApprovalException(
                    "Only PENDING expenditure can be approved");
        }

        User approver = userRepository
                .findById(request.getApprovedByUserId())
                .orElseThrow(() ->
                        new ExpenditureApprovalException(
                                "Approving user not found with id: "
                                        + request.getApprovedByUserId()));

        if (!"ACTIVE".equals(approver.getStatus())) {
            throw new ExpenditureApprovalException(
                    "Approving user is not active");
        }

        String roleName = approver.getRole() == null ? null : approver.getRole().getName();
        if (!"ADMIN".equals(roleName)
                && !"BASE_COMMANDER".equals(roleName)
                && !"LOGISTICS_OFFICER".equals(roleName)) {
            throw new ExpenditureApprovalException(
                    "User is not authorized to approve expenditures");
        }
        if (!"ADMIN".equals(roleName)
                && (approver.getBase() == null
                    || !approver.getBase().getId().equals(expenditure.getBase().getId()))) {
            throw new ExpenditureApprovalException(
                    "User is not authorized for this base");
        }

        Inventory inventory = inventoryRepository
                .findByBaseIdAndEquipmentTypeId(
                        expenditure.getBase().getId(),
                        expenditure.getEquipmentType().getId())
                .orElseThrow(() ->
                        new ExpenditureApprovalException(
                                "Inventory not found"));

        int quantity = expenditure.getQuantity();

        /*
         * Direct expenditure:
         * AVAILABLE -> EXPENDED
         *
         * Assignment expenditure:
         * ASSIGNED -> EXPENDED
         */
        if (expenditure.getAssignment() == null) {

            if (inventory.getAvailableQuantity() < quantity) {
                throw new ExpenditureApprovalException(
                        "Insufficient available inventory");
            }

            inventory.setAvailableQuantity(
                    inventory.getAvailableQuantity() - quantity);

        } else {

            Assignment assignment =
                    expenditure.getAssignment();

            if (!"ACTIVE".equals(assignment.getStatus())) {
                throw new ExpenditureApprovalException(
                        "Assignment is no longer active");
            }

            if (assignment.getQuantity() < quantity) {
                throw new ExpenditureApprovalException(
                        "Expenditure quantity exceeds assigned quantity");
            }

            if (inventory.getAssignedQuantity() < quantity) {
                throw new ExpenditureApprovalException(
                        "Insufficient assigned inventory");
            }

            inventory.setAssignedQuantity(
                    inventory.getAssignedQuantity() - quantity);

            assignment.setQuantity(
                    assignment.getQuantity() - quantity);

            /*
             * If the entire assignment has been expended,
             * close the assignment.
             */
            if (assignment.getQuantity() == 0) {
                assignment.setStatus("EXPENDED");
            }

            assignmentRepository.save(assignment);
        }

        // Expenditure removes inventory from total.
        inventory.setTotalQuantity(
                inventory.getTotalQuantity() - quantity);

        inventoryRepository.save(inventory);

        expenditure.setStatus("APPROVED");
        expenditure.setApprovedBy(approver);
        expenditure.setApprovedAt(
                java.time.LocalDateTime.now());

        Expenditure savedExpenditure =
                expenditureRepository.save(expenditure);

        // Ledger entry
        InventoryLedger ledger = new InventoryLedger();

        ledger.setBase(expenditure.getBase());
        ledger.setEquipmentType(
                expenditure.getEquipmentType());
        ledger.setTransactionType("EXPENDITURE");
        ledger.setTransactionId(savedExpenditure.getId());
        ledger.setQuantityIn(0);
        ledger.setQuantityOut(quantity);
        ledger.setBalanceAfter(
                inventory.getTotalQuantity());
        ledger.setCreatedBy(approver);

        inventoryLedgerRepository.save(ledger);

        // Audit entry
        AuditLog auditLog = new AuditLog();

        auditLog.setUser(approver);
        auditLog.setAction("EXPENDITURE_APPROVED");
        auditLog.setEntityType("EXPENDITURE");
        auditLog.setEntityId(savedExpenditure.getId());

        auditLogRepository.save(auditLog);

        return mapToResponse(savedExpenditure);
    }
    
    public ExpenditureResponse getExpenditureById(Long id) {

        Expenditure expenditure = expenditureRepository.findById(id)
                .orElseThrow(() ->
                        new ExpenditureNotFoundException(
                                "Expenditure not found with id: " + id));

        return mapToResponse(expenditure);
    }
    
    public List<ExpenditureResponse> getAllExpenditures() {

        return expenditureRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    @Transactional
    public ExpenditureResponse rejectExpenditure(
            Long expenditureId,
            Long rejectedByUserId) {

        Expenditure expenditure = expenditureRepository.findById(expenditureId)
                .orElseThrow(() ->
                        new ExpenditureNotFoundException(
                                "Expenditure not found with id: " + expenditureId));

        if (!"PENDING".equals(expenditure.getStatus())) {
            throw new ExpenditureApprovalException(
                    "Only PENDING expenditure can be rejected");
        }

        User user = userRepository.findById(rejectedByUserId)
                .orElseThrow(() ->
                        new ExpenditureApprovalException(
                                "User not found with id: " + rejectedByUserId));

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new ExpenditureApprovalException(
                    "User is not active");
        }

        String roleName = user.getRole() == null ? null : user.getRole().getName();
        if (!"ADMIN".equals(roleName)
                && !"BASE_COMMANDER".equals(roleName)
                && !"LOGISTICS_OFFICER".equals(roleName)) {
            throw new ExpenditureApprovalException(
                    "User is not authorized to reject expenditures");
        }
        if (!"ADMIN".equals(roleName)
                && (user.getBase() == null
                    || !user.getBase().getId().equals(expenditure.getBase().getId()))) {
            throw new ExpenditureApprovalException(
                    "User is not authorized for this base");
        }

        expenditure.setStatus("REJECTED");
        expenditure.setApprovedBy(user);
        expenditure.setApprovedAt(
                java.time.LocalDateTime.now());

        Expenditure saved = expenditureRepository.save(expenditure);

        AuditLog auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setAction("EXPENDITURE_REJECTED");
        auditLog.setEntityType("EXPENDITURE");
        auditLog.setEntityId(saved.getId());

        auditLogRepository.save(auditLog);

        return mapToResponse(saved);
    }
}
