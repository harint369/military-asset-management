package com.militaryasset.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TransferResponse {

    private Long id;

    private Long fromBaseId;
    private String fromBaseName;

    private Long toBaseId;
    private String toBaseName;

    private String status;

    private Long requestedBy;
    private String requestedByName;

    private Long approvedBy;
    private String approvedByName;

    private Long dispatchedBy;
    private String dispatchedByName;

    private Long receivedBy;
    private String receivedByName;

    private LocalDateTime approvedAt;
    private LocalDateTime dispatchedAt;
    private LocalDateTime receivedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<TransferItemResponse> items;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFromBaseId() {
		return fromBaseId;
	}

	public void setFromBaseId(Long fromBaseId) {
		this.fromBaseId = fromBaseId;
	}

	public String getFromBaseName() {
		return fromBaseName;
	}

	public void setFromBaseName(String fromBaseName) {
		this.fromBaseName = fromBaseName;
	}

	public Long getToBaseId() {
		return toBaseId;
	}

	public void setToBaseId(Long toBaseId) {
		this.toBaseId = toBaseId;
	}

	public String getToBaseName() {
		return toBaseName;
	}

	public void setToBaseName(String toBaseName) {
		this.toBaseName = toBaseName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(Long requestedBy) {
		this.requestedBy = requestedBy;
	}

	public String getRequestedByName() {
		return requestedByName;
	}

	public void setRequestedByName(String requestedByName) {
		this.requestedByName = requestedByName;
	}

	public Long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(Long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public String getApprovedByName() {
		return approvedByName;
	}

	public void setApprovedByName(String approvedByName) {
		this.approvedByName = approvedByName;
	}

	public Long getDispatchedBy() {
		return dispatchedBy;
	}

	public void setDispatchedBy(Long dispatchedBy) {
		this.dispatchedBy = dispatchedBy;
	}

	public String getDispatchedByName() {
		return dispatchedByName;
	}

	public void setDispatchedByName(String dispatchedByName) {
		this.dispatchedByName = dispatchedByName;
	}

	public Long getReceivedBy() {
		return receivedBy;
	}

	public void setReceivedBy(Long receivedBy) {
		this.receivedBy = receivedBy;
	}

	public String getReceivedByName() {
		return receivedByName;
	}

	public void setReceivedByName(String receivedByName) {
		this.receivedByName = receivedByName;
	}

	public LocalDateTime getApprovedAt() {
		return approvedAt;
	}

	public void setApprovedAt(LocalDateTime approvedAt) {
		this.approvedAt = approvedAt;
	}

	public LocalDateTime getDispatchedAt() {
		return dispatchedAt;
	}

	public void setDispatchedAt(LocalDateTime dispatchedAt) {
		this.dispatchedAt = dispatchedAt;
	}

	public LocalDateTime getReceivedAt() {
		return receivedAt;
	}

	public void setReceivedAt(LocalDateTime receivedAt) {
		this.receivedAt = receivedAt;
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

	public List<TransferItemResponse> getItems() {
		return items;
	}

	public void setItems(List<TransferItemResponse> items) {
		this.items = items;
	}

   
}
