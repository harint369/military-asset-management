package com.militaryasset.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AssignmentCreateRequest {

    @NotNull
    @Min(1)
    private Long baseId;

    @NotNull
    @Min(1)
    private Long equipmentTypeId;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotBlank
    @Size(max = 30)
    private String assignedToType;

    @NotNull
    @Min(1)
    private Long assignedToId;

    @NotNull
    @Min(1)
    private Long createdByUserId;

    public Long getBaseId() {
        return baseId;
    }

    public void setBaseId(Long baseId) {
        this.baseId = baseId;
    }

    public Long getEquipmentTypeId() {
        return equipmentTypeId;
    }

    public void setEquipmentTypeId(Long equipmentTypeId) {
        this.equipmentTypeId = equipmentTypeId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getAssignedToType() {
        return assignedToType;
    }

    public void setAssignedToType(String assignedToType) {
        this.assignedToType = assignedToType;
    }

    public Long getAssignedToId() {
        return assignedToId;
    }

    public void setAssignedToId(Long assignedToId) {
        this.assignedToId = assignedToId;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}
