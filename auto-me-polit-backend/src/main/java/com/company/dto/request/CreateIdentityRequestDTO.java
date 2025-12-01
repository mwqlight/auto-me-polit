package com.company.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

/**
 * 统一身份认证平台 - 身份创建请求DTO
 */
public class CreateIdentityRequestDTO {

    @NotBlank(message = "生物特征模板哈希不能为空")
    @Size(max = 128, message = "生物特征模板哈希长度不能超过128个字符")
    @Pattern(regexp = "^[a-fA-F0-9]+$", message = "生物特征模板哈希必须为十六进制格式")
    private String biometricTemplateHash;

    @NotBlank(message = "设备指纹不能为空")
    @Size(max = 255, message = "设备指纹长度不能超过255个字符")
    private String deviceFingerprint;

    @Size(max = 500, message = "恢复联系人配置长度不能超过500个字符")
    private String recoveryContacts; // JSON格式

    // 构造方法
    public CreateIdentityRequestDTO() {}

    public CreateIdentityRequestDTO(String biometricTemplateHash, String deviceFingerprint, String recoveryContacts) {
        this.biometricTemplateHash = biometricTemplateHash;
        this.deviceFingerprint = deviceFingerprint;
        this.recoveryContacts = recoveryContacts;
    }

    // Getter and Setter methods
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

    public String getRecoveryContacts() {
        return recoveryContacts;
    }

    public void setRecoveryContacts(String recoveryContacts) {
        this.recoveryContacts = recoveryContacts;
    }

    @Override
    public String toString() {
        return "CreateIdentityRequestDTO{" +
                "biometricTemplateHash='" + (biometricTemplateHash != null ? biometricTemplateHash.substring(0, Math.min(8, biometricTemplateHash.length())) + "..." : null) + '\'' +
                ", deviceFingerprint='" + (deviceFingerprint != null ? deviceFingerprint.substring(0, Math.min(8, deviceFingerprint.length())) + "..." : null) + '\'' +
                ", recoveryContacts='" + (recoveryContacts != null ? recoveryContacts.substring(0, Math.min(20, recoveryContacts.length())) + "..." : null) + '\'' +
                '}';
    }
}