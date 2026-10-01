package com.militaryasset.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ExpenditureCreateRequest {

    @NotNull
    @Min(1)
    private Long baseId;

    @NotNull
    @Min(1)
    private Long equipmentTypeId;

    private Long assignmentId;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotBlank
    @Size(max = 255)
    private String reason;

    @NotNull
    private LocalDateTime expendedAt;

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

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getExpendedAt() {
        return expendedAt;
    }

    public void setExpendedAt(LocalDateTime expendedAt) {
        this.expendedAt = expendedAt;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}
