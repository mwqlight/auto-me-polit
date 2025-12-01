package com.company.controller;

import com.company.dto.request.CreateIdentityRequestDTO;
import com.company.dto.response.CreateIdentityResponseDTO;
import com.company.service.CoreIdentityService;
import com.company.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 统一身份认证平台 - 核心身份管理控制器
 */
@RestController
@RequestMapping("/api/v1/identity")
@Tag(name = "核心身份管理", description = "核心身份创建、管理、恢复等API")
public class CoreIdentityController {

    private static final Logger logger = LoggerFactory.getLogger(CoreIdentityController.class);

    @Autowired
    private CoreIdentityService coreIdentityService;

    @PostMapping("/core")
    @Operation(
        summary = "创建核心身份", 
        description = "创建新的全球唯一数字身份，生成密钥对和恢复分片"
    )
    public ResponseEntity<ApiResponse<CreateIdentityResponseDTO>> createIdentity(
            @Valid @RequestBody CreateIdentityRequestDTO request) {
        try {
            logger.info("收到创建身份请求: {}", request);
            
            CreateIdentityResponseDTO response = coreIdentityService.createIdentity(request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("核心身份创建成功", response));
                
        } catch (Exception e) {
            logger.error("创建身份失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("创建核心身份失败: " + e.getMessage()));
        }
    }

    @GetMapping("/{identityId}")
    @Operation(
        summary = "获取身份信息", 
        description = "根据身份ID获取核心身份详细信息"
    )
    public ResponseEntity<ApiResponse<Object>> getIdentity(@PathVariable String identityId) {
        try {
            logger.info("获取身份信息: {}", identityId);
            
            return ResponseEntity.ok(
                ApiResponse.success("获取身份信息成功", 
                    coreIdentityService.getIdentityById(identityId))
            );
            
        } catch (Exception e) {
            logger.error("获取身份信息失败: {}", identityId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("身份不存在: " + e.getMessage()));
        }
    }

    @PostMapping("/{identityId}/freeze")
    @Operation(
        summary = "冻结身份", 
        description = "紧急锁定指定身份，停止所有身份相关操作"
    )
    public ResponseEntity<ApiResponse<Void>> freezeIdentity(
            @PathVariable String identityId,
            @RequestParam String reason) {
        try {
            logger.info("冻结身份: {}, 原因: {}", identityId, reason);
            
            coreIdentityService.freezeIdentity(identityId, reason);
            
            return ResponseEntity.ok(
                ApiResponse.success("身份冻结成功", null)
            );
            
        } catch (Exception e) {
            logger.error("冻结身份失败: {}", identityId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("冻结身份失败: " + e.getMessage()));
        }
    }

    @PostMapping("/{identityId}/recover")
    @Operation(
        summary = "恢复身份", 
        description = "使用恢复令牌恢复被冻结的身份"
    )
    public ResponseEntity<ApiResponse<Void>> recoverIdentity(
            @PathVariable String identityId,
            @RequestParam String recoveryToken) {
        try {
            logger.info("恢复身份: {}", identityId);
            
            coreIdentityService.recoverIdentity(identityId, recoveryToken);
            
            return ResponseEntity.ok(
                ApiResponse.success("身份恢复成功", null)
            );
            
        } catch (Exception e) {
            logger.error("恢复身份失败: {}", identityId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("恢复身份失败: " + e.getMessage()));
        }
    }

    @PostMapping("/{identityId}/device-update")
    @Operation(
        summary = "更新设备指纹", 
        description = "更新身份绑定的设备指纹，用于新设备验证"
    )
    public ResponseEntity<ApiResponse<Void>> updateDeviceFingerprint(
            @PathVariable String identityId,
            @RequestParam String deviceFingerprint) {
        try {
            logger.info("更新设备指纹: {}", identityId);
            
            coreIdentityService.updateDeviceFingerprint(identityId, deviceFingerprint);
            
            return ResponseEntity.ok(
                ApiResponse.success("设备指纹更新成功", null)
            );
            
        } catch (Exception e) {
            logger.error("更新设备指纹失败: {}", identityId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("更新设备指纹失败: " + e.getMessage()));
        }
    }

    @PostMapping("/{identityId}/last-active")
    @Operation(
        summary = "更新最后活跃时间", 
        description = "更新身份最后活跃时间，用于活跃状态管理"
    )
    public ResponseEntity<ApiResponse<Void>> updateLastActiveTime(@PathVariable String identityId) {
        try {
            coreIdentityService.updateLastActiveTime(identityId);
            return ResponseEntity.ok(
                ApiResponse.success("更新最后活跃时间成功", null)
            );
        } catch (Exception e) {
            logger.error("更新最后活跃时间失败: {}", identityId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("更新最后活跃时间失败: " + e.getMessage()));
        }
    }

    @GetMapping("/{identityId}/security-validation")
    @Operation(
        summary = "验证安全级别", 
        description = "验证身份是否满足指定的安全级别要求"
    )
    public ResponseEntity<ApiResponse<Boolean>> validateSecurityLevel(
            @PathVariable String identityId,
            @RequestParam Integer requiredLevel) {
        try {
            boolean isValid = coreIdentityService.validateSecurityLevel(identityId, requiredLevel);
            return ResponseEntity.ok(
                ApiResponse.success("安全级别验证完成", isValid)
            );
        } catch (Exception e) {
            logger.error("安全级别验证失败: {}", identityId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("安全级别验证失败: " + e.getMessage()));
        }
    }
}