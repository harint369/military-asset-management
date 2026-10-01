package com.militaryasset.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class TransferReceiveRequest {

    @NotNull
    @Min(1)
    private Long receivedByUserId;

    public Long getReceivedByUserId() {
        return receivedByUserId;
    }

    public void setReceivedByUserId(Long receivedByUserId) {
        this.receivedByUserId = receivedByUserId;
    }
}
