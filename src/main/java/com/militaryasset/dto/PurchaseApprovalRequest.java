package com.militaryasset.dto;

import jakarta.validation.constraints.NotNull;

public class PurchaseApprovalRequest {

    @NotNull
    private Long approvedByUserId;

    public PurchaseApprovalRequest() {
    }

    public Long getApprovedByUserId() {
        return approvedByUserId;
    }

    public void setApprovedByUserId(Long approvedByUserId) {
        this.approvedByUserId = approvedByUserId;
    }
}
