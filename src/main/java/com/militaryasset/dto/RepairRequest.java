package com.militaryasset.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RepairRequest {

    @NotNull
    @Min(1)
    private Long baseId;

    @NotNull
    @Min(1)
    private Long equipmentTypeId;

    @NotNull
    @Min(1)
    private Integer quantity;

    /*
     * AVAILABLE or ASSIGNED
     */
    @NotNull
    private String sourceStatus;

    /*
     * Required only when sourceStatus = ASSIGNED
     */
    private Long assignmentId;

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

    public String getSourceStatus() {
        return sourceStatus;
    }

    public void setSourceStatus(String sourceStatus) {
        this.sourceStatus = sourceStatus;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}
