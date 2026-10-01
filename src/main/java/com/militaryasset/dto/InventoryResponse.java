package com.militaryasset.dto;

import java.time.LocalDateTime;

public class InventoryResponse {

    private Long id;

    private Long baseId;
    private String baseName;

    private Long equipmentTypeId;
    private String equipmentTypeName;

    private Integer totalQuantity;
    private Integer availableQuantity;
    private Integer assignedQuantity;
    private Integer committedQuantity;
    private Integer inTransferQuantity;
    private Integer repairQuantity;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public InventoryResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBaseId() {
        return baseId;
    }

    public void setBaseId(Long baseId) {
        this.baseId = baseId;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public Long getEquipmentTypeId() {
        return equipmentTypeId;
    }

    public void setEquipmentTypeId(Long equipmentTypeId) {
        this.equipmentTypeId = equipmentTypeId;
    }

    public String getEquipmentTypeName() {
        return equipmentTypeName;
    }

    public void setEquipmentTypeName(String equipmentTypeName) {
        this.equipmentTypeName = equipmentTypeName;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Integer getAssignedQuantity() {
        return assignedQuantity;
    }

    public void setAssignedQuantity(Integer assignedQuantity) {
        this.assignedQuantity = assignedQuantity;
    }

    public Integer getCommittedQuantity() {
        return committedQuantity;
    }

    public void setCommittedQuantity(Integer committedQuantity) {
        this.committedQuantity = committedQuantity;
    }

    public Integer getInTransferQuantity() {
        return inTransferQuantity;
    }

    public void setInTransferQuantity(Integer inTransferQuantity) {
        this.inTransferQuantity = inTransferQuantity;
    }

    public Integer getRepairQuantity() {
        return repairQuantity;
    }

    public void setRepairQuantity(Integer repairQuantity) {
        this.repairQuantity = repairQuantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
