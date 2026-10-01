package com.militaryasset.repository;

import com.militaryasset.entity.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentTypeRepository extends JpaRepository<EquipmentType, Long> {
	
	boolean existsByName(String name);
}
