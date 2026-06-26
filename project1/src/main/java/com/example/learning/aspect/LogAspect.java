package com.example.learning.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * AOP 切面 — 统一记录 Service 层方法耗时
 *
 * 核心概念：
 * - 切点(Pointcut)：定义在哪些方法上生效
 * - 通知(Advice)：@Around 环绕通知，可在方法前后执行逻辑
 * - 切面(Aspect)：切点 + 通知的组合
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    @Pointcut("execution(* com.example.learning.service..*(..))")
    public void serviceLayer() {
    }

    @Around("serviceLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long cost = System.currentTimeMillis() - start;
            log.info("[AOP] {} 执行耗时 {}ms", methodName, cost);
            return result;
        } catch (Throwable e) {
            log.warn("[AOP] {} 执行异常: {}", methodName, e.getMessage());
            throw e;
        }
    }
}
