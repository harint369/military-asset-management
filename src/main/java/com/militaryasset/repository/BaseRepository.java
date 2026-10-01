package com.militaryasset.repository;

import com.militaryasset.entity.Base;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseRepository extends JpaRepository<Base, Long> {
	
	boolean existsByCode(String code);
	
	boolean existsByName(String name);
}
