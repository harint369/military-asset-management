package com.militaryasset.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AssignmentReturnRequest {

    @NotNull
    @Min(1)
    private Long returnedByUserId;

    public Long getReturnedByUserId() {
        return returnedByUserId;
    }

    public void setReturnedByUserId(Long returnedByUserId) {
        this.returnedByUserId = returnedByUserId;
    }
}
