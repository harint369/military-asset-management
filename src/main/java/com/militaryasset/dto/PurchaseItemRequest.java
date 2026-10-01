package com.militaryasset.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


public class PurchaseItemRequest {

    @NotNull
    private Long equipmentTypeId;

    @NotNull
    @Min(1)
    private Integer quantity;

    
    public PurchaseItemRequest() {
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

    
}
