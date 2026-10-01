package com.militaryasset.repository;

import com.militaryasset.entity.PurchaseItem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long>{

	List<PurchaseItem> findByPurchaseId(Long purchaseId);
	
	List<PurchaseItem> findByPurchaseIdIn(List<Long> purchaseIds);
}
