package com.example.miniewallet.admin;

import org.springframework.stereotype.Service;

import com.example.miniewallet.common.domain.AdminAuditLog;
import com.example.miniewallet.common.repository.AuditLogRepository;
import com.example.miniewallet.common.security.CurrentUserResolver;

@Service
public class AdminAuditService {

    private final AuditLogRepository auditLogRepository;
    private final CurrentUserResolver currentUserResolver;

    public AdminAuditService(AuditLogRepository auditLogRepository,
                             CurrentUserResolver currentUserResolver) {
        this.auditLogRepository = auditLogRepository;
        this.currentUserResolver = currentUserResolver;
    }

    public void logAction(String action, String targetType, Long targetId) {
        AdminAuditLog auditLog = new AdminAuditLog(
                currentUserResolver.currentUserId(),
                action,
                targetType,
                targetId);
        auditLogRepository.save(auditLog);
    }
}
