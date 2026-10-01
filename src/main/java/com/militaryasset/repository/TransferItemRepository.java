package com.militaryasset.repository;

import com.militaryasset.entity.TransferItem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferItemRepository extends JpaRepository<TransferItem, Long>{

	List<TransferItem> findByTransferId(Long transferId);
}
