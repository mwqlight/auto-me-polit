package com.company.service.impl;

import com.company.dto.request.CreateIdentityRequestDTO;
import com.company.dto.response.CreateIdentityResponseDTO;
import com.company.entity.CoreIdentity;
import com.company.exception.IdentityException;
import com.company.repository.CoreIdentityRepository;
import com.company.service.CoreIdentityService;
import com.company.util.CryptoUtil;
import com.company.util.IdentityGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 统一身份认证平台 - 核心身份管理服务实现
 * 实现全球唯一数字身份创建与管理
 */
@Service
@Transactional
public class CoreIdentityServiceImpl implements CoreIdentityService {

    private static final Logger logger = LoggerFactory.getLogger(CoreIdentityServiceImpl.class);

    @Autowired
    private CoreIdentityRepository coreIdentityRepository;

    @Autowired
    private CryptoUtil cryptoUtil;

    @Autowired
    private IdentityGenerator identityGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public CreateIdentityResponseDTO createIdentity(CreateIdentityRequestDTO request) throws IdentityException {
        try {
            logger.info("开始创建新的核心身份");

            // 1. 生成全局唯一身份ID
            String identityId = generateIdentityId();
            logger.debug("生成的身份ID: {}", identityId);

            // 2. 生成密钥对（国密SM2 + NIST Ed25519混合）
            KeyPair keyPair = generateKeyPair();
            logger.debug("密钥对生成完成");

            // 3. 加密私钥（使用用户设备指纹+生物特征）
            String encryptedPrivateKey = cryptoUtil.encryptPrivateKey(
                keyPair.getPrivateKey(),
                request.getDeviceFingerprint(),
                request.getBiometricTemplateHash()
            );

            // 4. 创建密钥分片（Shamir's Secret Sharing）
            KeyFragments keyFragments = splitKey(keyPair.getPrivateKey(), 3, 5);

            // 5. 构建恢复配置
            Map<String, Object> recoveryConfig = buildRecoveryConfig(request, keyFragments);

            // 6. 创建核心身份实体
            CoreIdentity identity = new CoreIdentity();
            identity.setIdentityId(identityId);
            identity.setPublicKey(keyPair.getPublicKey());
            identity.setPrivateKeyEncrypted(encryptedPrivateKey);
            identity.setStatus(CoreIdentity.IdentityStatus.ACTIVE);
            identity.setSecurityLevel(5); // 最高安全级别
            identity.setBiometricTemplateHash(request.getBiometricTemplateHash());
            identity.setDeviceFingerprint(request.getDeviceFingerprint());
            identity.setRecoveryConfig(objectMapper.writeValueAsString(recoveryConfig));
            identity.setLastActiveTime(LocalDateTime.now());
            identity.setMetadata(buildMetadata(request));

            // 7. 保存到数据库
            CoreIdentity savedIdentity = coreIdentityRepository.save(identity);
            logger.info("核心身份创建成功: {}", identityId);

            // 8. 构建响应
            return buildCreateResponse(savedIdentity, keyFragments);

        } catch (Exception e) {
            logger.error("创建核心身份失败", e);
            throw new IdentityException("创建核心身份失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CoreIdentity getIdentityById(String identityId) throws IdentityException {
        return coreIdentityRepository.findByIdentityId(identityId)
            .orElseThrow(() -> new IdentityException("身份不存在: " + identityId));
    }

    @Override
    @Transactional
    public void updateLastActiveTime(String identityId) throws IdentityException {
        CoreIdentity identity = getIdentityById(identityId);
        identity.setLastActiveTime(LocalDateTime.now());
        coreIdentityRepository.save(identity);
    }

    @Override
    @Transactional
    public void freezeIdentity(String identityId, String reason) throws IdentityException {
        CoreIdentity identity = getIdentityById(identityId);
        identity.setStatus(CoreIdentity.IdentityStatus.FROZEN);
        // 记录冻结原因到元数据
        updateMetadata(identity, "freeze_reason", reason);
        coreIdentityRepository.save(identity);
        logger.warn("身份被冻结: {}, 原因: {}", identityId, reason);
    }

    @Override
    @Transactional
    public void recoverIdentity(String identityId, String recoveryToken) throws IdentityException {
        // 验证恢复令牌
        if (!validateRecoveryToken(identityId, recoveryToken)) {
            throw new IdentityException("恢复令牌验证失败");
        }

        CoreIdentity identity = getIdentityById(identityId);
        identity.setStatus(CoreIdentity.IdentityStatus.ACTIVE);
        // 更新恢复相关信息
        updateMetadata(identity, "last_recovery_time", LocalDateTime.now().toString());
        coreIdentityRepository.save(identity);
        logger.info("身份恢复成功: {}", identityId);
    }

    @Override
    @Transactional
    public void updateDeviceFingerprint(String identityId, String deviceFingerprint) throws IdentityException {
        CoreIdentity identity = getIdentityById(identityId);
        identity.setDeviceFingerprint(deviceFingerprint);
        coreIdentityRepository.save(identity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateSecurityLevel(String identityId, Integer requiredLevel) throws IdentityException {
        CoreIdentity identity = getIdentityById(identityId);
        return identity.getSecurityLevel() >= requiredLevel;
    }

    @Override
    public String generateIdentityId() {
        return identityGenerator.generateGlobalIdentityId();
    }

    @Override
    public KeyPair generateKeyPair() {
        return cryptoUtil.generateHybridKeyPair();
    }

    @Override
    public KeyFragments splitKey(String secret, int threshold, int totalShares) {
        return cryptoUtil.splitKey(secret, threshold, totalShares);
    }

    @Override
    public String combineFragments(KeyFragments fragments) {
        return cryptoUtil.combineFragments(fragments);
    }

    // 私有辅助方法

    private Map<String, Object> buildRecoveryConfig(CreateIdentityRequestDTO request, KeyFragments keyFragments) {
        Map<String, Object> config = new HashMap<>();
        config.put("threshold", keyFragments.getThreshold());
        config.put("totalShares", keyFragments.getTotalShares());
        
        // 构建分片分发配置
        List<Map<String, String>> fragments = new ArrayList<>();
        for (int i = 0; i < keyFragments.getFragments().length; i++) {
            Map<String, String> fragment = new HashMap<>();
            fragment.put("fragmentId", "frag-" + (i + 1));
            fragment.put("fragmentData", keyFragments.getFragments()[i]);
            fragments.add(fragment);
        }
        config.put("fragments", fragments);
        
        return config;
    }

    private String buildMetadata(CreateIdentityRequestDTO request) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("creation_device", request.getDeviceFingerprint());
        metadata.put("creation_time", LocalDateTime.now().toString());
        metadata.put("version", "1.0");
        metadata.put("security_features", Arrays.asList("biometric", "device_binding", "key_sharing"));
        
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (Exception e) {
            logger.warn("构建元数据失败", e);
            return "{}";
        }
    }

    private CreateIdentityResponseDTO buildCreateResponse(CoreIdentity identity, KeyFragments keyFragments) {
        try {
            // 构建恢复密钥分片响应
            Map<String, Object> fragments = new HashMap<>();
            fragments.put("threshold", keyFragments.getThreshold());
            fragments.put("totalShares", keyFragments.getTotalShares());
            
            List<Map<String, String>> fragmentList = new ArrayList<>();
            for (int i = 0; i < keyFragments.getFragments().length; i++) {
                Map<String, String> fragment = new HashMap<>();
                fragment.put("fragmentId", "frag-" + (i + 1));
                fragment.put("contact", "contact-" + (i + 1)); // 这里需要根据实际恢复联系人配置
                fragmentList.add(fragment);
            }
            fragments.put("fragments", fragmentList);

            return new CreateIdentityResponseDTO(
                identity.getIdentityId(),
                identity.getPublicKey(),
                objectMapper.writeValueAsString(fragments),
                true,
                identity.getCreationTime(),
                identity.getSecurityLevel()
            );
        } catch (Exception e) {
            logger.error("构建创建响应失败", e);
            throw new IdentityException("构建创建响应失败", e);
        }
    }

    private boolean validateRecoveryToken(String identityId, String recoveryToken) {
        // 这里需要实现恢复令牌的验证逻辑
        // 包括验证分片签名、时间戳等
        return true; // 简化实现
    }

    private void updateMetadata(CoreIdentity identity, String key, Object value) {
        try {
            Map<String, Object> metadata = objectMapper.readValue(
                identity.getMetadata() != null ? identity.getMetadata() : "{}", 
                Map.class
            );
            metadata.put(key, value);
            identity.setMetadata(objectMapper.writeValueAsString(metadata));
        } catch (Exception e) {
            logger.warn("更新元数据失败", e);
        }
    }
}