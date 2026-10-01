package com.militaryasset.repository;

import com.militaryasset.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long>{

}
