package com.militaryasset.repository;

import com.militaryasset.entity.InventoryLedger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryLedgerRepository
        extends JpaRepository<InventoryLedger, Long> {

    
}
