package com.company.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 统一身份认证平台 - 关联账户实体
 * 管理用户在各个平台的账户关联关系
 */
@Entity
@Table(name = "linked_account",
       indexes = {
           @Index(name = "idx_account_identifier", columnList = "account_identifier"),
           @Index(name = "idx_platform", columnList = "platform"),
           @Index(name = "idx_identity_platform", columnList = "identity_id, platform")
       })
public class LinkedAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "account_id", unique = true, nullable = false)
    private String accountId; // 关联账户唯一ID

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identity_id", nullable = false)
    private CoreIdentity coreIdentity;

    @NotNull
    @Size(max = 50)
    @Column(name = "platform", nullable = false, length = 50)
    private String platform; // 平台名称 (微信/支付宝/Google/Facebook等)

    @NotNull
    @Column(name = "account_identifier", nullable = false)
    private String accountIdentifier; // 平台上的账户标识

    @Column(name = "account_name")
    private String accountName; // 账户显示名称

    @Column(name = "account_avatar")
    private String accountAvatar; // 账户头像URL

    @Column(name = "credential_hash")
    private String credentialHash; // 凭证哈希（不存储明文）

    @Column(name = "credential_encrypted")
    private String credentialEncrypted; // 加密存储的凭证

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "sync_status", nullable = false)
    private SyncStatus syncStatus = SyncStatus.PENDING;

    @Column(name = "last_sync_time")
    private LocalDateTime lastSyncTime;

    @Column(name = "account_health")
    private Integer accountHealth = 100; // 账户健康度 0-100

    @Column(name = "risk_score")
    private Integer riskScore = 0; // 风险评分 0-100

    @CreationTimestamp
    @Column(name = "creation_time", updatable = false)
    private LocalDateTime creationTime;

    @UpdateTimestamp
    @Column(name = "last_update_time")
    private LocalDateTime lastUpdateTime;

    @Column(name = "metadata", columnDefinition = "json")
    private String metadata; // 扩展元数据

    public enum SyncStatus {
        PENDING,     // 待同步
        SYNCING,     // 同步中
        SYNCED,      // 已同步
        FAILED,      // 同步失败
        DISABLED     // 已禁用
    }

    // 构造函数
    public LinkedAccount() {}

    public LinkedAccount(String accountId, CoreIdentity coreIdentity, String platform, String accountIdentifier) {
        this.accountId = accountId;
        this.coreIdentity = coreIdentity;
        this.platform = platform;
        this.accountIdentifier = accountIdentifier;
    }

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public CoreIdentity getCoreIdentity() {
        return coreIdentity;
    }

    public void setCoreIdentity(CoreIdentity coreIdentity) {
        this.coreIdentity = coreIdentity;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getAccountIdentifier() {
        return accountIdentifier;
    }

    public void setAccountIdentifier(String accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountAvatar() {
        return accountAvatar;
    }

    public void setAccountAvatar(String accountAvatar) {
        this.accountAvatar = accountAvatar;
    }

    public String getCredentialHash() {
        return credentialHash;
    }

    public void setCredentialHash(String credentialHash) {
        this.credentialHash = credentialHash;
    }

    public String getCredentialEncrypted() {
        return credentialEncrypted;
    }

    public void setCredentialEncrypted(String credentialEncrypted) {
        this.credentialEncrypted = credentialEncrypted;
    }

    public SyncStatus getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }

    public LocalDateTime getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(LocalDateTime lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public Integer getAccountHealth() {
        return accountHealth;
    }

    public void setAccountHealth(Integer accountHealth) {
        this.accountHealth = accountHealth;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
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
        LinkedAccount that = (LinkedAccount) o;
        return Objects.equals(accountId, that.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId);
    }

    @Override
    public String toString() {
        return "LinkedAccount{" +
                "id=" + id +
                ", accountId='" + accountId + '\'' +
                ", platform='" + platform + '\'' +
                ", accountIdentifier='" + accountIdentifier + '\'' +
                ", syncStatus=" + syncStatus +
                ", accountHealth=" + accountHealth +
                '}';
    }
}