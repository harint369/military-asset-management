package com.militaryasset.dto;

import jakarta.validation.constraints.NotNull;

public class PurchaseRejectionRequest {

    @NotNull
    private Long rejectedByUserId;

    public PurchaseRejectionRequest() {
    }

    public Long getRejectedByUserId() {
        return rejectedByUserId;
    }

    public void setRejectedByUserId(Long rejectedByUserId) {
        this.rejectedByUserId = rejectedByUserId;
    }
}
