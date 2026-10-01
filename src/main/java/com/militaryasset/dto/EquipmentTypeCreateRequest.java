package com.militaryasset.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EquipmentTypeCreateRequest {

    @NotBlank(message = "Equipment type name is required")
    @Size(max = 100, message = "Equipment type name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;

    @NotBlank(message = "Unit of measure is required")
    @Size(max = 30, message = "Unit of measure must not exceed 30 characters")
    private String unitOfMeasure;

    @NotNull(message = "Minimum reserve is required")
    @Min(value = 0, message = "Minimum reserve cannot be negative")
    private Integer minimumReserve;

    @NotBlank(message = "Status is required")
    @Size(max = 20, message = "Status must not exceed 20 characters")
    private String status;

    public EquipmentTypeCreateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Integer getMinimumReserve() {
        return minimumReserve;
    }

    public void setMinimumReserve(Integer minimumReserve) {
        this.minimumReserve = minimumReserve;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
