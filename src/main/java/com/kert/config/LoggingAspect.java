package com.kert.config;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LoggingAspect {



    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Controller *)")
    public void controllerMethods() {}


    @Before("controllerMethods()")
    public void logBeforeRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String clientIp = getClientIpAddress(request);
        String uri = request.getRequestURI();
        String method = request.getMethod();
        logger.info("Request - Method: {}, URI: {}, Client IP: {}", method, uri, clientIp);
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "exception")
    public void logAfterThrowing(Exception exception) {
        logger.error("An error occurred: {}", exception.getMessage());
    }

    // 클라이언트 IP를 확인하는 메서드
    private String getClientIpAddress(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("X-Real-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        } else {
            clientIp = clientIp.split(",")[0]; // 여러 IP 값이 있을 경우 첫 번째 값만 사용
        }
        return clientIp;
    }
}