package com.militaryasset.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class PurchaseCreateRequest {

	@NotNull
	@Min(1)
	private Long baseId;
	
    @Size(max = 255)
    private String source;

    @NotNull
    private LocalDate purchaseDate;

    @NotNull
    @Min(1)
    private Long createdByUserId;

    @NotNull
    @Valid
    @Size(min = 1)
    private List<PurchaseItemRequest> items;

    public PurchaseCreateRequest() {
    }

	public Long getBaseId() {
		return baseId;
	}

	public void setBaseId(Long baseId) {
		this.baseId = baseId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public LocalDate getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(LocalDate purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

	public List<PurchaseItemRequest> getItems() {
		return items;
	}

	public void setItems(List<PurchaseItemRequest> items) {
		this.items = items;
	}

    
}
