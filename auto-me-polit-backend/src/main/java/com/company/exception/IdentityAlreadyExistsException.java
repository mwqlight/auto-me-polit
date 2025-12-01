package com.company.exception;

/**
 * 统一身份认证平台 - 身份已存在异常
 */
public class IdentityAlreadyExistsException extends RuntimeException {
    
    private String identityId;

    public IdentityAlreadyExistsException(String identityId) {
        super("身份已存在: " + identityId);
        this.identityId = identityId;
    }

    public IdentityAlreadyExistsException(String identityId, String message) {
        super(message);
        this.identityId = identityId;
    }

    public String getIdentityId() {
        return identityId;
    }
}