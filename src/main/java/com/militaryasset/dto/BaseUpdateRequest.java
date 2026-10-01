package com.militaryasset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BaseUpdateRequest {

    @NotBlank(message = "Base name is required")
    @Size(max = 100, message = "Base name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Base code is required")
    @Size(max = 20, message = "Base code must not exceed 20 characters")
    private String code;

    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;

    @NotBlank(message = "Base status is required")
    @Size(max = 20, message = "Base status must not exceed 20 characters")
    private String status;

    public BaseUpdateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
