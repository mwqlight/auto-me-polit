package com.company.exception;

import com.company.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一身份认证平台 - 全局异常处理器
 * 实现统一的错误响应格式和安全日志记录
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String requestId = generateRequestId(request);
        logger.warn("参数验证失败 - RequestId: {}, Path: {}, Errors: {}", requestId, request.getRequestURI(), errors);

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "参数验证失败", errors));
    }

    /**
     * 处理数据绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleBindException(
            BindException ex, HttpServletRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String requestId = generateRequestId(request);
        logger.warn("数据绑定失败 - RequestId: {}, Path: {}, Errors: {}", requestId, request.getRequestURI(), errors);

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "数据绑定失败", errors));
    }

    /**
     * 处理身份不存在异常
     */
    @ExceptionHandler(IdentityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleIdentityNotFoundException(
            IdentityNotFoundException ex, HttpServletRequest request) {
        
        String requestId = generateRequestId(request);
        logger.warn("身份不存在 - RequestId: {}, IdentityId: {}, Path: {}", 
                   requestId, ex.getIdentityId(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.notFound(ex.getMessage()));
    }

    /**
     * 处理身份已存在异常
     */
    @ExceptionHandler(IdentityAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleIdentityAlreadyExistsException(
            IdentityAlreadyExistsException ex, HttpServletRequest request) {
        
        String requestId = generateRequestId(request);
        logger.warn("身份已存在 - RequestId: {}, IdentityId: {}, Path: {}", 
                   requestId, ex.getIdentityId(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(409, ex.getMessage()));
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(
            BadCredentialsException ex, HttpServletRequest request) {
        
        String requestId = generateRequestId(request);
        logger.warn("认证失败 - RequestId: {}, Path: {}, Error: {}", 
                   requestId, request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.unauthorized("认证失败"));
    }

    /**
     * 处理授权异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        
        String requestId = generateRequestId(request);
        logger.warn("访问被拒绝 - RequestId: {}, Path: {}, Error: {}", 
                   requestId, request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.forbidden("访问被拒绝"));
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        
        String requestId = generateRequestId(request);
        logger.error("业务异常 - RequestId: {}, Path: {}, Error: {}", 
                    requestId, request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.valueOf(ex.getStatusCode()))
                .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    /**
     * 处理系统异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        String requestId = generateRequestId(request);
        logger.error("系统异常 - RequestId: {}, Path: {}, Error: {}", 
                    requestId, request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.serverError("系统内部错误"));
    }

    /**
     * 生成请求ID用于日志追踪
     */
    private String generateRequestId(HttpServletRequest request) {
        return request.getHeader("X-Request-ID") != null ? 
               request.getHeader("X-Request-ID") : 
               "REQ-" + System.currentTimeMillis();
    }
}