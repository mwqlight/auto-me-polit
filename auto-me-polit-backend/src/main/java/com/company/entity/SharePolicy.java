package com.company.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 统一身份认证平台 - 共享策略实体
 * 管理身份属性共享的策略规则
 */
@Entity
@Table(name = "share_policy",
       indexes = {
           @Index(name = "idx_policy_identity", columnList = "identity_id"),
           @Index(name = "idx_requester", columnList = "requester")
       })
public class SharePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "policy_id", unique = true, nullable = false)
    private String policyId; // 策略唯一标识

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identity_id", nullable = false)
    private CoreIdentity coreIdentity;

    @NotNull
    @Size(max = 255)
    @Column(name = "requester", nullable = false)
    private String requester; // 请求方标识（应用域名/ID）

    @NotNull
    @Column(name = "attributes", columnDefinition = "json", nullable = false)
    private String attributes; // 共享属性JSON配置

    @NotNull
    @Column(name = "duration", nullable = false)
    private String duration; // ISO8601持续时间格式 (PT1H表示1小时)

    @Column(name = "constraints", columnDefinition = "json")
    private String constraints; // 约束条件JSON（地理位置、设备类型等）

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "default_action", nullable = false)
    private ShareAction defaultAction = ShareAction.DENY;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "creation_time", updatable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_used_time")
    private LocalDateTime lastUsedTime;

    @Column(name = "usage_count", nullable = false)
    private Long usageCount = 0L;

    @Column(name = "metadata", columnDefinition = "json")
    private String metadata; // 扩展元数据

    public enum ShareAction {
        ALLOW,      // 允许
        DENY,       // 拒绝
        PROMPT,     // 询问用户
        CONDITIONAL // 条件允许
    }

    // 构造函数
    public SharePolicy() {}

    public SharePolicy(String policyId, CoreIdentity coreIdentity, String requester, String attributes) {
        this.policyId = policyId;
        this.coreIdentity = coreIdentity;
        this.requester = requester;
        this.attributes = attributes;
    }

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public CoreIdentity getCoreIdentity() {
        return coreIdentity;
    }

    public void setCoreIdentity(CoreIdentity coreIdentity) {
        this.coreIdentity = coreIdentity;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }

    public ShareAction getDefaultAction() {
        return defaultAction;
    }

    public void setDefaultAction(ShareAction defaultAction) {
        this.defaultAction = defaultAction;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(LocalDateTime lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }

    public Long getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Long usageCount) {
        this.usageCount = usageCount;
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
        SharePolicy that = (SharePolicy) o;
        return Objects.equals(policyId, that.policyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(policyId);
    }

    @Override
    public String toString() {
        return "SharePolicy{" +
                "id=" + id +
                ", policyId='" + policyId + '\'' +
                ", requester='" + requester + '\'' +
                ", defaultAction=" + defaultAction +
                ", isActive=" + isActive +
                ", creationTime=" + creationTime +
                '}';
    }
}