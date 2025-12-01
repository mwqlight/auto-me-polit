package com.company.repository;

import com.company.entity.CoreIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 统一身份认证平台 - 核心身份数据访问层
 */
@Repository
public interface CoreIdentityRepository extends JpaRepository<CoreIdentity, Long> {

    /**
     * 根据身份ID查找核心身份
     * 
     * @param identityId 身份ID
     * @return 核心身份实体
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ci FROM CoreIdentity ci WHERE ci.identityId = :identityId")
    Optional<CoreIdentity> findByIdentityId(@Param("identityId") String identityId);

    /**
     * 根据公钥查找核心身份
     * 
     * @param publicKey 公钥
     * @return 核心身份实体
     */
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT ci FROM CoreIdentity ci WHERE ci.publicKey = :publicKey")
    Optional<CoreIdentity> findByPublicKey(@Param("publicKey") String publicKey);

    /**
     * 根据状态查找核心身份
     * 
     * @param status 身份状态
     * @return 核心身份列表
     */
    @Query("SELECT ci FROM CoreIdentity ci WHERE ci.status = :status")
    List<CoreIdentity> findByStatus(@Param("status") CoreIdentity.IdentityStatus status);

    /**
     * 查找需要更新的身份（最后活跃时间超过阈值）
     * 
     * @param thresholdTime 阈值时间
     * @return 需要更新的身份列表
     */
    @Query("SELECT ci FROM CoreIdentity ci WHERE ci.lastActiveTime < :thresholdTime OR ci.lastActiveTime IS NULL")
    List<CoreIdentity> findInactiveIdentities(@Param("thresholdTime") LocalDateTime thresholdTime);

    /**
     * 查找高风险身份（安全级别低于阈值或风险评分高）
     * 
     * @param minSecurityLevel 最小安全级别
     * @return 高风险身份列表
     */
    @Query("SELECT ci FROM CoreIdentity ci WHERE ci.securityLevel < :minSecurityLevel")
    List<CoreIdentity> findHighRiskIdentities(@Param("minSecurityLevel") Integer minSecurityLevel);

    /**
     * 统计各状态的身份数量
     * 
     * @return 各状态数量统计
     */
    @Query("SELECT ci.status, COUNT(ci) FROM CoreIdentity ci GROUP BY ci.status")
    List<Object[]> countByStatus();

    /**
     * 查找最近创建的身份
     * 
     * @param hoursAgo 几小时前
     * @return 最近创建的身份列表
     */
    @Query("SELECT ci FROM CoreIdentity ci WHERE ci.creationTime >= :startTime ORDER BY ci.creationTime DESC")
    List<CoreIdentity> findRecentlyCreated(@Param("startTime") LocalDateTime startTime);

    /**
     * 验证身份ID是否存在
     * 
     * @param identityId 身份ID
     * @return 是否存在
     */
    @Query("SELECT CASE WHEN COUNT(ci) > 0 THEN true ELSE false END FROM CoreIdentity ci WHERE ci.identityId = :identityId")
    boolean existsByIdentityId(@Param("identityId") String identityId);
}