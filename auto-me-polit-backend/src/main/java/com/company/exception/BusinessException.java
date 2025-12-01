package com.company.exception;

/**
 * 统一身份认证平台 - 业务异常
 */
public class BusinessException extends RuntimeException {
    
    private int code;
    private int statusCode;

    public BusinessException(String message) {
        super(message);
        this.code = 10001;
        this.statusCode = 400;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.statusCode = 400;
    }

    public BusinessException(int code, int statusCode, String message) {
        super(message);
        this.code = code;
        this.statusCode = statusCode;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 10001;
        this.statusCode = 400;
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.statusCode = 400;
    }

    public BusinessException(int code, int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.statusCode = statusCode;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}