package com.company.dto.response;

import java.time.LocalDateTime;

/**
 * 统一身份认证平台 - 身份创建响应DTO
 */
public class CreateIdentityResponseDTO {

    private String identityId;
    private String publicKey;
    private String recoveryKeyFragments; // JSON格式
    private Boolean setupComplete;
    private LocalDateTime creationTime;
    private Integer securityLevel;

    // 构造方法
    public CreateIdentityResponseDTO() {}

    public CreateIdentityResponseDTO(String identityId, String publicKey, String recoveryKeyFragments, Boolean setupComplete, LocalDateTime creationTime, Integer securityLevel) {
        this.identityId = identityId;
        this.publicKey = publicKey;
        this.recoveryKeyFragments = recoveryKeyFragments;
        this.setupComplete = setupComplete;
        this.creationTime = creationTime;
        this.securityLevel = securityLevel;
    }

    // Getter and Setter methods
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

    public String getRecoveryKeyFragments() {
        return recoveryKeyFragments;
    }

    public void setRecoveryKeyFragments(String recoveryKeyFragments) {
        this.recoveryKeyFragments = recoveryKeyFragments;
    }

    public Boolean getSetupComplete() {
        return setupComplete;
    }

    public void setSetupComplete(Boolean setupComplete) {
        this.setupComplete = setupComplete;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public Integer getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(Integer securityLevel) {
        this.securityLevel = securityLevel;
    }

    @Override
    public String toString() {
        return "CreateIdentityResponseDTO{" +
                "identityId='" + identityId + '\'' +
                ", publicKey='" + (publicKey != null ? publicKey.substring(0, Math.min(8, publicKey.length())) + "..." : null) + '\'' +
                ", recoveryKeyFragments='" + (recoveryKeyFragments != null ? recoveryKeyFragments.substring(0, Math.min(20, recoveryKeyFragments.length())) + "..." : null) + '\'' +
                ", setupComplete=" + setupComplete +
                ", creationTime=" + creationTime +
                ", securityLevel=" + securityLevel +
                '}';
    }
}