package com.company.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 统一身份认证平台 - 身份ID生成器
 * 实现全球唯一数字身份ID生成
 */
@Component
public class IdentityGenerator {

    private static final String IDENTITY_PREFIX = "UID-GLOBAL-";
    private static final String HEX_CHARS = "0123456789ABCDEF";
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final AtomicLong counter = new AtomicLong(0);

    /**
     * 生成全局唯一身份ID
     * 格式: UID-GLOBAL-xxxxxxxx (19-25字符长度)
     * 
     * @return 全局唯一身份ID
     */
    public String generateGlobalIdentityId() {
        // 使用时间戳 + 随机数 + 计数器确保唯一性
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        
        // 生成8位随机十六进制字符
        String randomHex = generateRandomHex(8);
        
        // 添加计数器确保同一秒内的唯一性
        String counterHex = Long.toHexString(counter.incrementAndGet() & 0xFFFF);
        while (counterHex.length() < 4) {
            counterHex = "0" + counterHex;
        }
        
        return IDENTITY_PREFIX + timestamp + randomHex + counterHex;
    }

    /**
     * 生成短格式身份ID（用于显示）
     * 格式: UID-xxxxxxxx
     * 
     * @return 短格式身份ID
     */
    public String generateShortIdentityId() {
        String randomHex = generateRandomHex(8);
        return "UID-" + randomHex;
    }

    /**
     * 生成账户关联ID
     * 格式: ACC-xxxxxxxx
     * 
     * @return 账户关联ID
     */
    public String generateAccountId() {
        String randomHex = generateRandomHex(8);
        return "ACC-" + randomHex;
    }

    /**
     * 生成共享策略ID
     * 格式: POL-xxxxxxxx
     * 
     * @return 共享策略ID
     */
    public String generatePolicyId() {
        String randomHex = generateRandomHex(8);
        return "POL-" + randomHex;
    }

    /**
     * 生成授权ID
     * 格式: AUTH-xxxxxxxx
     * 
     * @return 授权ID
     */
    public String generateAuthorizationId() {
        String randomHex = generateRandomHex(8);
        return "AUTH-" + randomHex;
    }

    /**
     * 生成设备指纹
     * 
     * @return 设备指纹
     */
    public String generateDeviceFingerprint() {
        StringBuilder fingerprint = new StringBuilder();
        
        // 添加时间戳
        fingerprint.append(Long.toHexString(System.currentTimeMillis()));
        
        // 添加随机数据
        fingerprint.append(generateRandomHex(16));
        
        // 添加系统属性
        fingerprint.append(System.getProperty("os.name").hashCode());
        fingerprint.append(System.getProperty("java.version").hashCode());
        
        return fingerprint.toString().toUpperCase();
    }

    /**
     * 生成生物特征模板哈希
     * 
     * @param biometricData 生物特征数据
     * @return 哈希值
     */
    public String generateBiometricHash(String biometricData) {
        // 这里应该集成真实的生物特征哈希算法
        // 简化实现，使用SHA-256哈希
        return org.apache.commons.codec.digest.DigestUtils.sha256Hex(biometricData);
    }

    /**
     * 验证身份ID格式
     * 
     * @param identityId 身份ID
     * @return 是否有效
     */
    public boolean isValidIdentityId(String identityId) {
        return identityId != null && 
               identityId.startsWith(IDENTITY_PREFIX) && 
               identityId.length() >= 19 && 
               identityId.length() <= 25;
    }

    /**
     * 从身份ID中提取时间戳
     * 
     * @param identityId 身份ID
     * @return 创建时间
     */
    public LocalDateTime extractCreationTime(String identityId) {
        if (!isValidIdentityId(identityId)) {
            return null;
        }
        
        try {
            String timePart = identityId.substring(IDENTITY_PREFIX.length(), IDENTITY_PREFIX.length() + 14);
            return LocalDateTime.parse(timePart, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成随机十六进制字符串
     * 
     * @param length 长度
     * @return 十六进制字符串
     */
    private String generateRandomHex(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(HEX_CHARS.length());
            sb.append(HEX_CHARS.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 重置计数器（主要用于测试）
     */
    public void resetCounter() {
        counter.set(0);
    }

    /**
     * 获取下一个计数器值
     * 
     * @return 计数器值
     */
    public long getNextCounter() {
        return counter.incrementAndGet();
    }
}