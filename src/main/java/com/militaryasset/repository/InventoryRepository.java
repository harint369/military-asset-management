package com.militaryasset.repository;

import com.militaryasset.entity.Inventory;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long>{

	
	Optional<Inventory> findByBaseIdAndEquipmentTypeId(
	        Long baseId,
	        Long equipmentTypeId
	);
	
	List<Inventory> findByBaseId(Long baseId);
	
	List<Inventory> findByEquipmentTypeId(Long equipmentTypeId);
}
