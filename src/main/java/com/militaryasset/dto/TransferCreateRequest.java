package com.militaryasset.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class TransferCreateRequest {

    @NotNull
    @Min(1)
    private Long fromBaseId;

    @NotNull
    @Min(1)
    private Long toBaseId;

    @NotNull
    @Min(1)
    private Long requestedByUserId;

    @NotNull
    @Size(min = 1)
    @Valid
    private List<TransferItemRequest> items;

    public Long getFromBaseId() {
        return fromBaseId;
    }

    public void setFromBaseId(Long fromBaseId) {
        this.fromBaseId = fromBaseId;
    }

    public Long getToBaseId() {
        return toBaseId;
    }

    public void setToBaseId(Long toBaseId) {
        this.toBaseId = toBaseId;
    }

    public Long getRequestedByUserId() {
        return requestedByUserId;
    }

    public void setRequestedByUserId(Long requestedByUserId) {
        this.requestedByUserId = requestedByUserId;
    }

    public List<TransferItemRequest> getItems() {
        return items;
    }

    public void setItems(List<TransferItemRequest> items) {
        this.items = items;
    }
}
