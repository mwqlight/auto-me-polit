package com.company.service;

import com.company.dto.request.CreateIdentityRequestDTO;
import com.company.dto.response.CreateIdentityResponseDTO;
import com.company.entity.CoreIdentity;
import com.company.exception.IdentityException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 统一身份认证平台 - 核心身份管理服务接口
 */
public interface CoreIdentityService {

    /**
     * 创建新的核心身份
     * 
     * @param request 身份创建请求
     * @return 身份创建响应
     * @throws IdentityException 身份创建异常
     */
    @Transactional
    CreateIdentityResponseDTO createIdentity(CreateIdentityRequestDTO request) throws IdentityException;

    /**
     * 根据身份ID获取身份信息
     * 
     * @param identityId 身份ID
     * @return 身份实体
     * @throws IdentityException 身份不存在异常
     */
    @Transactional(readOnly = true)
    CoreIdentity getIdentityById(String identityId) throws IdentityException;

    /**
     * 更新身份最后活跃时间
     * 
     * @param identityId 身份ID
     * @throws IdentityException 身份不存在异常
     */
    @Transactional
    void updateLastActiveTime(String identityId) throws IdentityException;

    /**
     * 冻结身份（紧急锁定）
     * 
     * @param identityId 身份ID
     * @param reason 冻结原因
     * @throws IdentityException 身份不存在异常
     */
    @Transactional
    void freezeIdentity(String identityId, String reason) throws IdentityException;

    /**
     * 恢复身份状态
     * 
     * @param identityId 身份ID
     * @param recoveryToken 恢复令牌
     * @throws IdentityException 恢复失败异常
     */
    @Transactional
    void recoverIdentity(String identityId, String recoveryToken) throws IdentityException;

    /**
     * 更新设备指纹
     * 
     * @param identityId 身份ID
     * @param deviceFingerprint 新的设备指纹
     * @throws IdentityException 身份不存在异常
     */
    @Transactional
    void updateDeviceFingerprint(String identityId, String deviceFingerprint) throws IdentityException;

    /**
     * 验证身份安全级别
     * 
     * @param identityId 身份ID
     * @param requiredLevel 所需安全级别
     * @return 是否满足要求
     * @throws IdentityException 身份不存在异常
     */
    @Transactional(readOnly = true)
    boolean validateSecurityLevel(String identityId, Integer requiredLevel) throws IdentityException;

    /**
     * 生成身份唯一ID
     * 
     * @return 全局唯一身份ID
     */
    String generateIdentityId();

    /**
     * 生成密钥对
     * 
     * @return 公钥私钥对
     */
    KeyPair generateKeyPair();

    /**
     * 密钥分片（Shamir's Secret Sharing）
     * 
     * @param secret 原始密钥
     * @param threshold 门限值
     * @param totalShares 总分片数
     * @return 密钥分片
     */
    KeyFragments splitKey(String secret, int threshold, int totalShares);

    /**
     * 密钥重组
     * 
     * @param fragments 密钥分片
     * @return 原始密钥
     */
    String combineFragments(KeyFragments fragments);

    // 内部类定义
    class KeyPair {
        private final String publicKey;
        private final String privateKey;

        public KeyPair(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public String getPublicKey() { return publicKey; }
        public String getPrivateKey() { return privateKey; }
    }

    class KeyFragments {
        private final String[] fragments;
        private final int threshold;
        private final int totalShares;

        public KeyFragments(String[] fragments, int threshold, int totalShares) {
            this.fragments = fragments;
            this.threshold = threshold;
            this.totalShares = totalShares;
        }

        public String[] getFragments() { return fragments; }
        public int getThreshold() { return threshold; }
        public int getTotalShares() { return totalShares; }
    }
}