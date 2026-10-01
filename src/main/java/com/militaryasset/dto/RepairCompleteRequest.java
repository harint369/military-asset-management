package com.militaryasset.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RepairCompleteRequest {

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
    private String destinationStatus;

    /*
     * Required when destinationStatus = ASSIGNED
     */
    private Long assignmentId;

    @NotNull
    @Min(1)
    private Long completedByUserId;

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

    public String getDestinationStatus() {
        return destinationStatus;
    }

    public void setDestinationStatus(String destinationStatus) {
        this.destinationStatus = destinationStatus;
    }

    public Long getCompletedByUserId() {
        return completedByUserId;
    }

    public void setCompletedByUserId(Long completedByUserId) {
        this.completedByUserId = completedByUserId;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }
}
