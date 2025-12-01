package com.company;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * 统一身份认证平台 - 主启动类
 * 支持分布式身份认证、多因子验证、零信任架构
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@EnableResourceServer
@EnableAuthorizationServer
public class AutoMePolitApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoMePolitApplication.class, args);
        System.out.println("""
            
            ╔══════════════════════════════════════════════════════════════════╗
            ║                 🚀 统一身份认证平台启动成功 🚀                     ║
            ║                                                                  ║
            ║  🆔 全球唯一数字身份管理                                          ║
            ║  🔐 多因素身份验证系统                                            ║
            ║  🔗 身份代理与同步引擎                                            ║
            ║  🌐 身份共享与控制中心                                            ║
            ║  🛡️  军工级安全架构设计                                          ║
            ║                                                                  ║
            ║  📊 驾驶舱界面: http://localhost:3000                              ║
            ║  📚 API文档: http://localhost:8080/swagger-ui.html                 ║
            ║  🏥 健康检查: http://localhost:8080/actuator/health               ║
            ╚══════════════════════════════════════════════════════════════════╝
            """);
    }
}