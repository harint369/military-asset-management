package com.militaryasset.service;

import com.militaryasset.dto.DashboardResponse;

import com.militaryasset.entity.Inventory;
import com.militaryasset.entity.InventoryLedger;
import com.militaryasset.exception.DashboardValidationException;
import com.militaryasset.repository.BaseRepository;
import com.militaryasset.repository.EquipmentTypeRepository;
import com.militaryasset.repository.InventoryLedgerRepository;
import com.militaryasset.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {

    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryLedgerRepository ledgerRepository;

    public DashboardService(BaseRepository baseRepository,
                            EquipmentTypeRepository equipmentTypeRepository,
                            InventoryRepository inventoryRepository,
                            InventoryLedgerRepository ledgerRepository) {
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.inventoryRepository = inventoryRepository;
        this.ledgerRepository = ledgerRepository;
    }

    public DashboardResponse getDashboard(Long baseId, Long equipmentTypeId,
                                          LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null || fromDate.isAfter(toDate)) {
            throw new DashboardValidationException("Invalid date range");
        }

        if (!baseRepository.existsById(baseId)) {
            throw new DashboardValidationException("Base not found with id: " + baseId);
        }

        if (!equipmentTypeRepository.existsById(equipmentTypeId)) {
            throw new DashboardValidationException(
                    "Equipment type not found with id: " + equipmentTypeId);
        }

        Inventory inventory = inventoryRepository
                .findByBaseIdAndEquipmentTypeId(baseId, equipmentTypeId)
                .orElseThrow(() -> new DashboardValidationException(
                        "Inventory not found for the selected base and equipment"));

        List<InventoryLedger> entries = ledgerRepository.findAll().stream()
                .filter(e -> e.getBase().getId().equals(baseId))
                .filter(e -> e.getEquipmentType().getId().equals(equipmentTypeId))
                .toList();

        int opening = 0;
        int purchases = 0;
        int transferIn = 0;
        int transferOut = 0;
        int expended = 0;

        InventoryLedger latestBefore = entries.stream()
                .filter(e -> e.getCreatedAt() != null
                        && e.getCreatedAt().toLocalDate().isBefore(fromDate))
                .max(Comparator.comparing(InventoryLedger::getCreatedAt)
                        .thenComparing(InventoryLedger::getId))
                .orElse(null);

        if (latestBefore != null) {
            opening = latestBefore.getBalanceAfter();
        } else {
            opening = entries.stream()
                    .filter(e -> "OPENING_BALANCE".equals(e.getTransactionType()))
                    .filter(e -> e.getCreatedAt() != null
                            && !e.getCreatedAt().toLocalDate().isAfter(toDate))
                    .max((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                    .map(InventoryLedger::getBalanceAfter)
                    .orElse(0);
        }

        for (InventoryLedger entry : entries) {
            if (entry.getCreatedAt() == null) {
                continue;
            }

            LocalDate date = entry.getCreatedAt().toLocalDate();
            if (date.isBefore(fromDate) || date.isAfter(toDate)) {
                continue;
            }

            switch (entry.getTransactionType()) {
                case "PURCHASE" -> purchases += entry.getQuantityIn();
                case "TRANSFER_IN" -> transferIn += entry.getQuantityIn();
                case "TRANSFER_OUT" -> transferOut += entry.getQuantityOut();
                case "EXPENDITURE" -> expended += entry.getQuantityOut();
                default -> { }
            }
        }

        DashboardResponse response = new DashboardResponse();
        response.setBaseId(baseId);
        response.setEquipmentTypeId(equipmentTypeId);
        response.setOpeningBalance(opening);
        response.setPurchases(purchases);
        response.setTransferIn(transferIn);
        response.setTransferOut(transferOut);
        response.setNetMovement(purchases + transferIn - transferOut);
        response.setAssigned(inventory.getAssignedQuantity());
        response.setExpended(expended);
        response.setClosingBalance(
                opening + purchases + transferIn - transferOut - expended);

        return response;
    }
}
