package com.militaryasset.dto;

import java.time.LocalDateTime;

public class EquipmentTypeResponse {

    private Long id;
    private String name;
    private String category;
    private String unitOfMeasure;
    private Integer minimumReserve;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EquipmentTypeResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
