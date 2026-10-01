package com.militaryasset.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class TransferDispatchRequest {

    @NotNull
    @Min(1)
    private Long dispatchedByUserId;

    public Long getDispatchedByUserId() {
        return dispatchedByUserId;
    }

    public void setDispatchedByUserId(Long dispatchedByUserId) {
        this.dispatchedByUserId = dispatchedByUserId;
    }
}
