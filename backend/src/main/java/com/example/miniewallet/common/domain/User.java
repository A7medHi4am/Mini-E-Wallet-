package com.example.miniewallet.common.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(nullable = false)
    private String password;         

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private KycStatus kycStatus;

    @Column(nullable = false)
    private Instant createdAt;

    protected User() {
    }

    public User(String name, String email, String phone, String hashedPassword) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = hashedPassword;
        this.role = Role.USER;
        this.kycStatus = KycStatus.UNVERIFIED;
        this.createdAt = Instant.now();
    }

    public User(String name, String email, String phone, String hashedPassword, Role role) {
        this(name, email, phone, hashedPassword);
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public KycStatus getKycStatus() {
        return kycStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
