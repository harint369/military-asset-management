package com.militaryasset.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "inventory",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_inventory_base_equipment",
            columnNames = {"base_id", "equipment_type_id"}
        )
    }
)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "base_id", nullable = false)
    private Base base;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_type_id", nullable = false)
    private EquipmentType equipmentType;

    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity = 0;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity = 0;

    @Column(name = "assigned_quantity", nullable = false)
    private Integer assignedQuantity = 0;

    @Column(name = "committed_quantity", nullable = false)
    private Integer committedQuantity = 0;

    @Column(name = "in_transfer_quantity", nullable = false)
    private Integer inTransferQuantity = 0;

    @Column(name = "repair_quantity", nullable = false)
    private Integer repairQuantity = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Inventory() {
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Base getBase() {
		return base;
	}

	public void setBase(Base base) {
		this.base = base;
	}

	public EquipmentType getEquipmentType() {
		return equipmentType;
	}

	public void setEquipmentType(EquipmentType equipmentType) {
		this.equipmentType = equipmentType;
	}

	public Integer getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(Integer totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public Integer getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(Integer availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	public Integer getAssignedQuantity() {
		return assignedQuantity;
	}

	public void setAssignedQuantity(Integer assignedQuantity) {
		this.assignedQuantity = assignedQuantity;
	}

	public Integer getCommittedQuantity() {
		return committedQuantity;
	}

	public void setCommittedQuantity(Integer committedQuantity) {
		this.committedQuantity = committedQuantity;
	}

	public Integer getInTransferQuantity() {
		return inTransferQuantity;
	}

	public void setInTransferQuantity(Integer inTransferQuantity) {
		this.inTransferQuantity = inTransferQuantity;
	}

	public Integer getRepairQuantity() {
		return repairQuantity;
	}

	public void setRepairQuantity(Integer repairQuantity) {
		this.repairQuantity = repairQuantity;
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

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
