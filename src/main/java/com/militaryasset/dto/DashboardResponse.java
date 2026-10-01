package com.militaryasset.dto;

public class DashboardResponse {

    private Long baseId;
    private Long equipmentTypeId;

    private Integer openingBalance;
    private Integer purchases;
    private Integer transferIn;
    private Integer transferOut;
    private Integer netMovement;
    private Integer assigned;
    private Integer expended;
    private Integer closingBalance;

    public Long getBaseId() {
        return baseId;
    }

    public void setBaseId(Long baseId) {
        this.baseId = baseId;
    }

    public Long getEquipmentTypeId() {
        return equipmentTypeId;
    }

    public void setEquipmentTypeId(Long equipmentTypeId) {
        this.equipmentTypeId = equipmentTypeId;
    }

    public Integer getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(Integer openingBalance) {
        this.openingBalance = openingBalance;
    }

    public Integer getPurchases() {
        return purchases;
    }

    public void setPurchases(Integer purchases) {
        this.purchases = purchases;
    }

    public Integer getTransferIn() {
        return transferIn;
    }

    public void setTransferIn(Integer transferIn) {
        this.transferIn = transferIn;
    }

    public Integer getTransferOut() {
        return transferOut;
    }

    public void setTransferOut(Integer transferOut) {
        this.transferOut = transferOut;
    }

    public Integer getNetMovement() {
        return netMovement;
    }

    public void setNetMovement(Integer netMovement) {
        this.netMovement = netMovement;
    }

    public Integer getAssigned() {
        return assigned;
    }

    public void setAssigned(Integer assigned) {
        this.assigned = assigned;
    }

    public Integer getExpended() {
        return expended;
    }

    public void setExpended(Integer expended) {
        this.expended = expended;
    }

    public Integer getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(Integer closingBalance) {
        this.closingBalance = closingBalance;
    }
}
