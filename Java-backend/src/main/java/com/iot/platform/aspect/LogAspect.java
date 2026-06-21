package com.iot.platform.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.platform.annotation.OperationLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * AOP日志切面 - 统一记录Controller/Service/MQTT/WebSocket操作日志
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final ObjectMapper objectMapper;

    /**
     * Controller层 - 拦截所有controller方法，记录HTTP请求和响应
     */
    @Around("execution(* com.iot.platform.controller..*.*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();

        // 获取HTTP请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String requestUri = "";
        String httpMethod = "";
        String clientIp = "";
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            requestUri = request.getRequestURI();
            httpMethod = request.getMethod();
            clientIp = getClientIp(request);
        }

        // 检查是否有@OperationLog注解
        OperationLog opLog = method.getAnnotation(OperationLog.class);
        String operationDesc = opLog != null ? opLog.description() : "";

        // 记录请求日志
        String argsStr = formatArgs(joinPoint.getArgs());
        log.info("[API请求] {} {} | IP={} | {}.{}({}) | {}",
                httpMethod, requestUri, clientIp, className, methodName,
                argsStr, operationDesc);

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - startTime;

            // 记录响应日志
            String resultStr = "";
            try {
                resultStr = objectMapper.writeValueAsString(result);
                if (resultStr.length() > 500) {
                    resultStr = resultStr.substring(0, 500) + "...(truncated)";
                }
            } catch (Exception e) {
                resultStr = result.toString();
            }

            log.info("[API响应] {} {} | 耗时={}ms | 结果={}", httpMethod, requestUri, elapsed, resultStr);
            return result;

        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.error("[API异常] {} {} | 耗时={}ms | 异常={}", httpMethod, requestUri, elapsed, ex.getMessage());
            throw ex;
        }
    }

    /**
     * Service层 - 拦截所有service实现方法，记录业务操作
     */
    @Around("execution(* com.iot.platform.service.impl..*.*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();
        String argsStr = formatArgs(joinPoint.getArgs());

        log.debug("[业务开始] {}.{}({})", className, methodName, argsStr);

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - startTime;
            log.debug("[业务完成] {}.{} | 耗时={}ms", className, methodName, elapsed);
            return result;
        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.error("[业务异常] {}.{} | 耗时={}ms | 异常={}", className, methodName, elapsed, ex.getMessage());
            throw ex;
        }
    }

    /**
     * MQTT服务层 - 拦截MqttService的方法
     */
    @Around("execution(* com.iot.platform.mqtt.MqttService.*(..))")
    public Object logMqtt(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String argsStr = formatArgs(joinPoint.getArgs());

        log.info("[MQTT操作] {}({})", methodName, argsStr);

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("[MQTT完成] {} | 耗时={}ms", methodName, elapsed);
            return result;
        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.error("[MQTT异常] {} | 耗时={}ms | 异常={}", methodName, elapsed, ex.getMessage());
            throw ex;
        }
    }

    /**
     * WebSocket服务层 - 拦截WebSocketService的方法
     */
    @Around("execution(* com.iot.platform.websocket.WebSocketService.*(..))")
    public Object logWebSocket(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String argsStr = formatArgs(joinPoint.getArgs());

        log.info("[WebSocket推送] {}({})", methodName, argsStr);

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - startTime;
            log.debug("[WebSocket完成] {} | 耗时={}ms", methodName, elapsed);
            return result;
        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.error("[WebSocket异常] {} | 耗时={}ms | 异常={}", methodName, elapsed, ex.getMessage());
            throw ex;
        }
    }

    /**
     * 带@OperationLog注解的方法 - 额外记录操作审计日志
     */
    @AfterReturning(pointcut = "@annotation(com.iot.platform.annotation.OperationLog)", returning = "result")
    public void logOperation(JoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog opLog = method.getAnnotation(OperationLog.class);

        if (opLog != null) {
            log.info("[操作审计] 模块={} | 类型={} | 描述={} | 方法={}.{}",
                    opLog.module(), opLog.type(), opLog.description(),
                    joinPoint.getTarget().getClass().getSimpleName(), method.getName());
        }
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 格式化方法参数（避免打印过长内容）
     */
    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) return "";
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                if (i > 0) sb.append(", ");
                String argStr = objectMapper.writeValueAsString(args[i]);
                if (argStr.length() > 200) {
                    argStr = argStr.substring(0, 200) + "...";
                }
                sb.append(argStr);
            }
            return sb.toString();
        } catch (Exception e) {
            return Arrays.toString(args);
        }
    }
}
