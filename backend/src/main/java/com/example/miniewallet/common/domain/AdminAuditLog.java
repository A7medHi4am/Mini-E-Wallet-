package com.example.miniewallet.common.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin_audit_logs")
public class AdminAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admin_user_id", nullable = false)
    private Long adminUserId;

    @Column(nullable = false)
    private String action;

    @Column(name = "target_type", nullable = false)
    private String targetType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected AdminAuditLog() {
    }

    public AdminAuditLog(Long adminUserId, String action, String targetType, Long targetId) {
        this.adminUserId = adminUserId;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getAdminUserId() {
        return adminUserId;
    }

    public String getAction() {
        return action;
    }

    public String getTargetType() {
        return targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
