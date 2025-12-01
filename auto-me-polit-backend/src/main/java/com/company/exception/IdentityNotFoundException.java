package com.company.exception;

/**
 * 统一身份认证平台 - 身份不存在异常
 */
public class IdentityNotFoundException extends RuntimeException {
    
    private String identityId;

    public IdentityNotFoundException(String identityId) {
        super("身份不存在: " + identityId);
        this.identityId = identityId;
    }

    public IdentityNotFoundException(String identityId, String message) {
        super(message);
        this.identityId = identityId;
    }

    public String getIdentityId() {
        return identityId;
    }
}