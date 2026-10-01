package com.militaryasset.service;

import com.militaryasset.dto.TransferCreateRequest;
import com.militaryasset.dto.TransferItemRequest;
import com.militaryasset.dto.TransferItemResponse;
import com.militaryasset.dto.TransferResponse;
import com.militaryasset.entity.AuditLog;
import com.militaryasset.entity.Base;
import com.militaryasset.entity.EquipmentType;
import com.militaryasset.entity.Inventory;
import com.militaryasset.entity.InventoryLedger;
import com.militaryasset.entity.Transfer;
import com.militaryasset.entity.TransferItem;
import com.militaryasset.entity.User;
import com.militaryasset.exception.TransferNotFoundException;
import com.militaryasset.exception.TransferValidationException;
import com.militaryasset.exception.UserNotFoundException;
import com.militaryasset.repository.AuditLogRepository;
import com.militaryasset.repository.BaseRepository;
import com.militaryasset.repository.EquipmentTypeRepository;
import com.militaryasset.repository.InventoryLedgerRepository;
import com.militaryasset.repository.InventoryRepository;
import com.militaryasset.repository.TransferItemRepository;
import com.militaryasset.repository.TransferRepository;
import com.militaryasset.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final TransferItemRepository transferItemRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final InventoryLedgerRepository inventoryLedgerRepository;

    public TransferService(
            TransferRepository transferRepository,
            TransferItemRepository transferItemRepository,
            BaseRepository baseRepository,
            EquipmentTypeRepository equipmentTypeRepository,
            InventoryRepository inventoryRepository,
            UserRepository userRepository,
            AuditLogRepository auditLogRepository,
            InventoryLedgerRepository inventoryLedgerRepository) {
        this.transferRepository = transferRepository;
        this.transferItemRepository = transferItemRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
        this.inventoryLedgerRepository = inventoryLedgerRepository;
    }

    @Transactional
    public TransferResponse createTransfer(TransferCreateRequest request) {
        Base fromBase = getBase(request.getFromBaseId());
        Base toBase = getBase(request.getToBaseId());

        if (fromBase.getId().equals(toBase.getId())) {
            throw new TransferValidationException(
                    "Source and destination bases must be different");
        }
        checkActiveBase(fromBase);
        checkActiveBase(toBase);

        User requestedBy = getActiveUser(request.getRequestedByUserId());
        checkBaseAccess(requestedBy, fromBase, "create a transfer");

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new TransferValidationException(
                    "Transfer must contain at least one item");
        }

        Set<Long> equipmentIds = new HashSet<>();
        for (TransferItemRequest item : request.getItems()) {
            if (!equipmentIds.add(item.getEquipmentTypeId())) {
                throw new TransferValidationException(
                        "Duplicate equipment type in transfer: "
                                + item.getEquipmentTypeId());
            }

            EquipmentType equipmentType = getEquipmentType(item.getEquipmentTypeId());
            if (!"ACTIVE".equals(equipmentType.getStatus())) {
                throw new TransferValidationException(
                        "Equipment type is not active: " + equipmentType.getName());
            }

            Inventory inventory = getInventory(
                    fromBase.getId(), equipmentType.getId());

            int remaining = inventory.getAvailableQuantity() - item.getQuantity();
            if (remaining < 0) {
                throw new TransferValidationException(
                        "Insufficient available inventory for "
                                + equipmentType.getName());
            }
            if (remaining < equipmentType.getMinimumReserve()) {
                throw new TransferValidationException(
                        "Transfer would violate minimum reserve for "
                                + equipmentType.getName());
            }
        }

        Transfer transfer = new Transfer();
        transfer.setFromBase(fromBase);
        transfer.setToBase(toBase);
        transfer.setStatus("REQUESTED");
        transfer.setRequestedBy(requestedBy);

        Transfer savedTransfer = transferRepository.save(transfer);

        for (TransferItemRequest item : request.getItems()) {
            TransferItem transferItem = new TransferItem();
            transferItem.setTransfer(savedTransfer);
            transferItem.setEquipmentType(getEquipmentType(item.getEquipmentTypeId()));
            transferItem.setQuantity(item.getQuantity());
            transferItemRepository.save(transferItem);
        }

        saveAudit("TRANSFER_CREATED", savedTransfer, requestedBy);
        return mapToResponse(savedTransfer);
    }

    @Transactional
    public TransferResponse approveTransfer(Long transferId, Long approvedByUserId) {
        Transfer transfer = getTransfer(transferId);
        checkStatus(transfer, "REQUESTED");

        User approvedBy = getActiveUser(approvedByUserId);
        checkActionRole(approvedBy, "approve transfers");
        checkBaseAccess(approvedBy, transfer.getFromBase(), "approve this transfer");

        List<TransferItem> items = transferItemRepository.findByTransferId(transferId);
        for (TransferItem item : items) {
            Inventory inventory = getInventory(
                    transfer.getFromBase().getId(),
                    item.getEquipmentType().getId());

            int remaining = inventory.getAvailableQuantity() - item.getQuantity();
            if (remaining < item.getEquipmentType().getMinimumReserve()) {
                throw new TransferValidationException(
                        "Transfer would violate minimum reserve for "
                                + item.getEquipmentType().getName());
            }

            inventory.setAvailableQuantity(remaining);
            inventory.setCommittedQuantity(
                    inventory.getCommittedQuantity() + item.getQuantity());
            inventoryRepository.save(inventory);
        }

        transfer.setStatus("APPROVED");
        transfer.setApprovedBy(approvedBy);
        transfer.setApprovedAt(LocalDateTime.now());
        Transfer savedTransfer = transferRepository.save(transfer);

        saveAudit("TRANSFER_APPROVED", savedTransfer, approvedBy);
        return mapToResponse(savedTransfer);
    }

    @Transactional
    public TransferResponse dispatchTransfer(Long transferId, Long dispatchedByUserId) {
        Transfer transfer = getTransfer(transferId);
        checkStatus(transfer, "APPROVED");

        User dispatchedBy = getActiveUser(dispatchedByUserId);
        checkActionRole(dispatchedBy, "dispatch transfers");
        checkBaseAccess(dispatchedBy, transfer.getFromBase(), "dispatch this transfer");

        List<TransferItem> items = transferItemRepository.findByTransferId(transferId);
        for (TransferItem item : items) {
            Inventory inventory = getInventory(
                    transfer.getFromBase().getId(),
                    item.getEquipmentType().getId());

            if (inventory.getCommittedQuantity() < item.getQuantity()) {
                throw new TransferValidationException(
                        "Insufficient committed quantity for "
                                + item.getEquipmentType().getName());
            }

            inventory.setCommittedQuantity(
                    inventory.getCommittedQuantity() - item.getQuantity());
            inventory.setInTransferQuantity(
                    inventory.getInTransferQuantity() + item.getQuantity());
            inventoryRepository.save(inventory);
        }

        transfer.setStatus("IN_TRANSFER");
        transfer.setDispatchedBy(dispatchedBy);
        transfer.setDispatchedAt(LocalDateTime.now());
        Transfer savedTransfer = transferRepository.save(transfer);

        saveAudit("TRANSFER_DISPATCHED", savedTransfer, dispatchedBy);
        return mapToResponse(savedTransfer);
    }

    @Transactional
    public TransferResponse receiveTransfer(Long transferId, Long receivedByUserId) {
        Transfer transfer = getTransfer(transferId);
        checkStatus(transfer, "IN_TRANSFER");

        User receivedBy = getActiveUser(receivedByUserId);
        checkActionRole(receivedBy, "receive transfers");
        checkBaseAccess(receivedBy, transfer.getToBase(), "receive this transfer");

        List<TransferItem> items = transferItemRepository.findByTransferId(transferId);

        for (TransferItem item : items) {
            Inventory source = getInventory(
                    transfer.getFromBase().getId(),
                    item.getEquipmentType().getId());

            if (source.getInTransferQuantity() < item.getQuantity()) {
                throw new TransferValidationException(
                        "Insufficient in-transfer quantity for "
                                + item.getEquipmentType().getName());
            }

            source.setInTransferQuantity(
                    source.getInTransferQuantity() - item.getQuantity());
            source.setTotalQuantity(
                    source.getTotalQuantity() - item.getQuantity());
            inventoryRepository.save(source);

            saveLedger(
                    transfer.getFromBase(),
                    item.getEquipmentType(),
                    "TRANSFER_OUT",
                    transfer.getId(),
                    0,
                    item.getQuantity(),
                    source.getTotalQuantity(),
                    receivedBy);
        }

        for (TransferItem item : items) {
            Inventory destination = getInventory(
                    transfer.getToBase().getId(),
                    item.getEquipmentType().getId());

            destination.setTotalQuantity(
                    destination.getTotalQuantity() + item.getQuantity());
            destination.setAvailableQuantity(
                    destination.getAvailableQuantity() + item.getQuantity());
            inventoryRepository.save(destination);

            saveLedger(
                    transfer.getToBase(),
                    item.getEquipmentType(),
                    "TRANSFER_IN",
                    transfer.getId(),
                    item.getQuantity(),
                    0,
                    destination.getTotalQuantity(),
                    receivedBy);
        }

        transfer.setStatus("RECEIVED");
        transfer.setReceivedBy(receivedBy);
        transfer.setReceivedAt(LocalDateTime.now());
        Transfer savedTransfer = transferRepository.save(transfer);

        saveAudit("TRANSFER_RECEIVED", savedTransfer, receivedBy);
        return mapToResponse(savedTransfer);
    }

    @Transactional
    public TransferResponse completeTransfer(Long transferId) {
        Transfer transfer = getTransfer(transferId);
        checkStatus(transfer, "RECEIVED");

        transfer.setStatus("COMPLETED");
        Transfer savedTransfer = transferRepository.save(transfer);
        saveAudit("TRANSFER_COMPLETED", savedTransfer, transfer.getReceivedBy());

        return mapToResponse(savedTransfer);
    }

    public TransferResponse getTransferById(Long id) {
        return mapToResponse(getTransfer(id));
    }

    public List<TransferResponse> getAllTransfers() {
        return transferRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private Transfer getTransfer(Long id) {
        return transferRepository.findById(id)
                .orElseThrow(() ->
                        new TransferNotFoundException(
                                "Transfer not found with id: " + id));
    }

    private Base getBase(Long id) {
        return baseRepository.findById(id)
                .orElseThrow(() ->
                        new TransferValidationException(
                                "Base not found with id: " + id));
    }

    private EquipmentType getEquipmentType(Long id) {
        return equipmentTypeRepository.findById(id)
                .orElseThrow(() ->
                        new TransferValidationException(
                                "Equipment type not found with id: " + id));
    }

    private Inventory getInventory(Long baseId, Long equipmentTypeId) {
        return inventoryRepository.findByBaseIdAndEquipmentTypeId(
                        baseId, equipmentTypeId)
                .orElseThrow(() ->
                        new TransferValidationException(
                                "Inventory not found for the selected base and equipment type"));
    }

    private User getActiveUser(Long id) {
        if (id == null || id <= 0) {
            throw new TransferValidationException("User ID must be greater than zero");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + id));

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new TransferValidationException("User is not active");
        }
        return user;
    }

    private void checkActiveBase(Base base) {
        if (!"ACTIVE".equals(base.getStatus())) {
            throw new TransferValidationException(
                    "Base is not active: " + base.getName());
        }
    }

    private void checkBaseAccess(User user, Base base, String action) {
        if (user.getRole() == null) {
            throw new TransferValidationException("User has no assigned role");
        }

        if (!"ADMIN".equals(user.getRole().getName())
                && (user.getBase() == null
                || !user.getBase().getId().equals(base.getId()))) {
            throw new TransferValidationException(
                    "User is not authorized to " + action);
        }
    }

    private void checkActionRole(User user, String action) {
        String role = user.getRole() == null ? null : user.getRole().getName();
        if (!"ADMIN".equals(role)
                && !"BASE_COMMANDER".equals(role)
                && !"LOGISTICS_OFFICER".equals(role)) {
            throw new TransferValidationException(
                    "User is not authorized to " + action);
        }
    }

    private void checkStatus(Transfer transfer, String expectedStatus) {
        if (!expectedStatus.equals(transfer.getStatus())) {
            throw new TransferValidationException(
                    "Transfer cannot be processed from status "
                            + transfer.getStatus());
        }
    }

    private void saveAudit(String action, Transfer transfer, User user) {
        AuditLog audit = new AuditLog();
        audit.setUser(user);
        audit.setAction(action);
        audit.setEntityType("TRANSFER");
        audit.setEntityId(transfer.getId());
        auditLogRepository.save(audit);
    }

    private void saveLedger(
            Base base,
            EquipmentType equipmentType,
            String type,
            Long transferId,
            int quantityIn,
            int quantityOut,
            int balanceAfter,
            User user) {

        InventoryLedger ledger = new InventoryLedger();
        ledger.setBase(base);
        ledger.setEquipmentType(equipmentType);
        ledger.setTransactionType(type);
        ledger.setTransactionId(transferId);
        ledger.setQuantityIn(quantityIn);
        ledger.setQuantityOut(quantityOut);
        ledger.setBalanceAfter(balanceAfter);
        ledger.setCreatedBy(user);
        inventoryLedgerRepository.save(ledger);
    }

    private TransferResponse mapToResponse(Transfer transfer) {
        TransferResponse response = new TransferResponse();

        response.setId(transfer.getId());
        response.setFromBaseId(transfer.getFromBase().getId());
        response.setFromBaseName(transfer.getFromBase().getName());
        response.setToBaseId(transfer.getToBase().getId());
        response.setToBaseName(transfer.getToBase().getName());
        response.setStatus(transfer.getStatus());
        response.setRequestedBy(transfer.getRequestedBy().getId());
        response.setRequestedByName(transfer.getRequestedBy().getFullName());

        if (transfer.getApprovedBy() != null) {
            response.setApprovedBy(transfer.getApprovedBy().getId());
            response.setApprovedByName(transfer.getApprovedBy().getFullName());
        }
        if (transfer.getDispatchedBy() != null) {
            response.setDispatchedBy(transfer.getDispatchedBy().getId());
            response.setDispatchedByName(transfer.getDispatchedBy().getFullName());
        }
        if (transfer.getReceivedBy() != null) {
            response.setReceivedBy(transfer.getReceivedBy().getId());
            response.setReceivedByName(transfer.getReceivedBy().getFullName());
        }

        response.setApprovedAt(transfer.getApprovedAt());
        response.setDispatchedAt(transfer.getDispatchedAt());
        response.setReceivedAt(transfer.getReceivedAt());
        response.setCreatedAt(transfer.getCreatedAt());
        response.setUpdatedAt(transfer.getUpdatedAt());

        List<TransferItemResponse> itemResponses =
                transferItemRepository.findByTransferId(transfer.getId())
                        .stream()
                        .map(item -> {
                            TransferItemResponse itemResponse =
                                    new TransferItemResponse();
                            itemResponse.setId(item.getId());
                            itemResponse.setEquipmentTypeId(
                                    item.getEquipmentType().getId());
                            itemResponse.setEquipmentTypeName(
                                    item.getEquipmentType().getName());
                            itemResponse.setQuantity(item.getQuantity());
                            return itemResponse;
                        })
                        .toList();

        response.setItems(itemResponses);
        return response;
    }
}
