package com.company.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 统一身份认证平台 - 核心身份实体
 * 实现全球唯一数字身份生成与管理
 */
@Entity
@Table(name = "core_identity", 
       indexes = {
           @Index(name = "idx_identity_id", columnList = "identity_id"),
           @Index(name = "idx_public_key", columnList = "public_key"),
           @Index(name = "idx_status", columnList = "status")
       })
public class CoreIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 19, max = 25)
    @Column(name = "identity_id", unique = true, nullable = false, length = 25)
    private String identityId; // 格式: UID-GLOBAL-xxxxxxxx

    @NotNull
    @Column(name = "public_key", nullable = false)
    private String publicKey;

    @Column(name = "private_key_encrypted")
    private String privateKeyEncrypted; // 加密存储的私钥

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private IdentityStatus status = IdentityStatus.ACTIVE;

    @NotNull
    @Column(name = "security_level", nullable = false)
    private Integer securityLevel; // 1-5级安全等级

    @Column(name = "biometric_template_hash")
    private String biometricTemplateHash; // 生物特征模板哈希（不存储原始数据）

    @Column(name = "device_fingerprint")
    private String deviceFingerprint; // 设备指纹

    @Column(name = "recovery_config", columnDefinition = "json")
    private String recoveryConfig; // 恢复配置JSON

    @CreationTimestamp
    @Column(name = "creation_time", updatable = false)
    private LocalDateTime creationTime;

    @UpdateTimestamp
    @Column(name = "last_update_time")
    private LocalDateTime lastUpdateTime;

    @Column(name = "last_active_time")
    private LocalDateTime lastActiveTime;

    @Column(name = "metadata", columnDefinition = "json")
    private String metadata; // 扩展元数据

    public enum IdentityStatus {
        ACTIVE,      // 活跃
        DORMANT,     // 休眠
        FROZEN,      // 冻结
        REVOKED,     // 撤销
        RECOVERY     // 恢复中
    }

    // 构造函数
    public CoreIdentity() {}

    public CoreIdentity(String identityId, String publicKey, Integer securityLevel) {
        this.identityId = identityId;
        this.publicKey = publicKey;
        this.securityLevel = securityLevel;
    }

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKeyEncrypted() {
        return privateKeyEncrypted;
    }

    public void setPrivateKeyEncrypted(String privateKeyEncrypted) {
        this.privateKeyEncrypted = privateKeyEncrypted;
    }

    public IdentityStatus getStatus() {
        return status;
    }

    public void setStatus(IdentityStatus status) {
        this.status = status;
    }

    public Integer getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(Integer securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getBiometricTemplateHash() {
        return biometricTemplateHash;
    }

    public void setBiometricTemplateHash(String biometricTemplateHash) {
        this.biometricTemplateHash = biometricTemplateHash;
    }

    public String getDeviceFingerprint() {
        return deviceFingerprint;
    }

    public void setDeviceFingerprint(String deviceFingerprint) {
        this.deviceFingerprint = deviceFingerprint;
    }

    public String getRecoveryConfig() {
        return recoveryConfig;
    }

    public void setRecoveryConfig(String recoveryConfig) {
        this.recoveryConfig = recoveryConfig;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public LocalDateTime getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(LocalDateTime lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoreIdentity that = (CoreIdentity) o;
        return Objects.equals(identityId, that.identityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identityId);
    }

    @Override
    public String toString() {
        return "CoreIdentity{" +
                "id=" + id +
                ", identityId='" + identityId + '\'' +
                ", status=" + status +
                ", securityLevel=" + securityLevel +
                ", creationTime=" + creationTime +
                '}';
    }
}