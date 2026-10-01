package com.militaryasset.service;

import com.militaryasset.dto.RepairCompleteRequest;
import com.militaryasset.dto.RepairRequest;
import com.militaryasset.dto.RepairResponse;
import com.militaryasset.entity.Assignment;
import com.militaryasset.entity.Base;
import com.militaryasset.entity.EquipmentType;
import com.militaryasset.entity.Inventory;
import com.militaryasset.entity.User;
import com.militaryasset.exception.RepairValidationException;
import com.militaryasset.repository.AssignmentRepository;
import com.militaryasset.repository.BaseRepository;
import com.militaryasset.repository.EquipmentTypeRepository;
import com.militaryasset.repository.InventoryRepository;
import com.militaryasset.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RepairService {

    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final InventoryRepository inventoryRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    public RepairService(
            BaseRepository baseRepository,
            EquipmentTypeRepository equipmentTypeRepository,
            InventoryRepository inventoryRepository,
            AssignmentRepository assignmentRepository,
            UserRepository userRepository) {

        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.inventoryRepository = inventoryRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RepairResponse startRepair(RepairRequest request) {

        Base base = baseRepository.findById(request.getBaseId())
                .orElseThrow(() ->
                        new RepairValidationException(
                                "Base not found"));

        if (!"ACTIVE".equals(base.getStatus())) {
            throw new RepairValidationException(
                    "Base is not active");
        }

        EquipmentType equipmentType =
                equipmentTypeRepository.findById(
                        request.getEquipmentTypeId())
                .orElseThrow(() ->
                        new RepairValidationException(
                                "Equipment type not found"));

        User user = userRepository.findById(
                        request.getCreatedByUserId())
                .orElseThrow(() ->
                        new RepairValidationException(
                                "User not found"));

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new RepairValidationException(
                    "User is not active");
        }

        if (user.getRole() == null) {
            throw new RepairValidationException("User has no assigned role");
        }
        if (!"ADMIN".equals(user.getRole().getName())
                && (user.getBase() == null || !user.getBase().getId().equals(base.getId()))) {
            throw new RepairValidationException(
                    "User is not authorized for this base");
        }

        if (request.getQuantity() == null
                || request.getQuantity() <= 0) {

            throw new RepairValidationException(
                    "Repair quantity must be greater than 0");
        }

        Inventory inventory =
                inventoryRepository
                        .findByBaseIdAndEquipmentTypeId(
                                base.getId(),
                                equipmentType.getId())
                        .orElseThrow(() ->
                                new RepairValidationException(
                                        "Inventory not found"));

        String source = request.getSourceStatus();

        if ("AVAILABLE".equals(source)) {

            if (inventory.getAvailableQuantity()
                    < request.getQuantity()) {

                throw new RepairValidationException(
                        "Insufficient available quantity");
            }

            inventory.setAvailableQuantity(
                    inventory.getAvailableQuantity()
                            - request.getQuantity());

        } else if ("ASSIGNED".equals(source)) {

            if (request.getAssignmentId() == null) {
                throw new RepairValidationException(
                        "Assignment ID is required");
            }

            Assignment assignment =
                    assignmentRepository.findById(
                            request.getAssignmentId())
                    .orElseThrow(() ->
                            new RepairValidationException(
                                    "Assignment not found"));

            if (!"ACTIVE".equals(assignment.getStatus())) {
                throw new RepairValidationException(
                        "Assignment is not active");
            }

            if (!assignment.getBase().getId().equals(base.getId())
                    || !assignment.getEquipmentType().getId().equals(equipmentType.getId())) {
                throw new RepairValidationException(
                        "Assignment does not match the selected base and equipment type");
            }

            if (assignment.getQuantity()
                    < request.getQuantity()) {

                throw new RepairValidationException(
                        "Repair quantity exceeds assigned quantity");
            }

            if (inventory.getAssignedQuantity()
                    < request.getQuantity()) {

                throw new RepairValidationException(
                        "Insufficient assigned quantity");
            }

            inventory.setAssignedQuantity(
                    inventory.getAssignedQuantity()
                            - request.getQuantity());

        } else {
            throw new RepairValidationException(
                    "Source status must be AVAILABLE or ASSIGNED");
        }

        inventory.setRepairQuantity(
                inventory.getRepairQuantity()
                        + request.getQuantity());

        inventoryRepository.save(inventory);

        RepairResponse response = new RepairResponse();
        response.setBaseId(base.getId());
        response.setEquipmentTypeId(equipmentType.getId());
        response.setQuantity(request.getQuantity());
        response.setSourceStatus(source);
        response.setAssignmentId(request.getAssignmentId());
        response.setMessage("Equipment moved to repair");

        return response;
    }
    
    @Transactional
    public RepairResponse completeRepair(
            RepairCompleteRequest request) {

        User user = userRepository.findById(request.getCompletedByUserId())
                .orElseThrow(() -> new RepairValidationException("User not found"));
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new RepairValidationException("User is not active");
        }
        if (user.getRole() == null) {
            throw new RepairValidationException("User has no assigned role");
        }
        if (!"ADMIN".equals(user.getRole().getName())
                && (user.getBase() == null || !user.getBase().getId().equals(request.getBaseId()))) {
            throw new RepairValidationException("User is not authorized for this base");
        }

        Inventory inventory = inventoryRepository
                .findByBaseIdAndEquipmentTypeId(
                        request.getBaseId(),
                        request.getEquipmentTypeId())
                .orElseThrow(() ->
                        new RepairValidationException(
                                "Inventory not found"));

        int quantity = request.getQuantity();

        if (inventory.getRepairQuantity() < quantity) {
            throw new RepairValidationException(
                    "Insufficient quantity in repair");
        }

        String destination = request.getDestinationStatus();

        if ("AVAILABLE".equals(destination)) {

            inventory.setRepairQuantity(
                    inventory.getRepairQuantity() - quantity);

            inventory.setAvailableQuantity(
                    inventory.getAvailableQuantity() + quantity);

        } else if ("ASSIGNED".equals(destination)) {

            if (request.getAssignmentId() == null) {
                throw new RepairValidationException(
                        "Assignment ID is required");
            }

            Assignment assignment =
                    assignmentRepository.findById(
                            request.getAssignmentId())
                    .orElseThrow(() ->
                            new RepairValidationException(
                                    "Assignment not found"));

            if (!"ACTIVE".equals(assignment.getStatus())) {
                throw new RepairValidationException(
                        "Assignment is not active");
            }

            if (!assignment.getBase().getId()
                    .equals(request.getBaseId())) {

                throw new RepairValidationException(
                        "Assignment does not belong to this base");
            }

            if (!assignment.getEquipmentType().getId()
                    .equals(request.getEquipmentTypeId())) {

                throw new RepairValidationException(
                        "Assignment equipment type does not match");
            }

            inventory.setRepairQuantity(
                    inventory.getRepairQuantity() - quantity);

            inventory.setAssignedQuantity(
                    inventory.getAssignedQuantity() + quantity);

        } else {

            throw new RepairValidationException(
                    "Destination status must be AVAILABLE or ASSIGNED");
        }

        inventoryRepository.save(inventory);

        RepairResponse response = new RepairResponse();

        response.setBaseId(request.getBaseId());
        response.setEquipmentTypeId(request.getEquipmentTypeId());
        response.setQuantity(quantity);
        response.setSourceStatus("REPAIR");
        response.setAssignmentId(request.getAssignmentId());
        response.setMessage("Equipment repair completed");

        return response;
    }
}
