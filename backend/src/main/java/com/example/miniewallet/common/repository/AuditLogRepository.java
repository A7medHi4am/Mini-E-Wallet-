package com.example.miniewallet.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.miniewallet.common.domain.AdminAuditLog;

/**
 * Shared repository for AdminAuditLog.
 */
public interface AuditLogRepository extends JpaRepository<AdminAuditLog, Long> {
}
