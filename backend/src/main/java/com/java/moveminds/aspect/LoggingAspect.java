package com.java.moveminds.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    // Log method entry for controllers
    @Before("execution(* com.java.moveminds.controllers..*(..))")
    public void logControllerEntry(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        log.info("➡ Entering controller: {} with arguments: {}", methodName, args);
    }

    // Log method exit for controllers
    @AfterReturning(pointcut = "execution(* com.java.moveminds.controllers..*(..))", returning = "result")
    public void logControllerExit(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("✅ Exiting controller: {} with result: {}", methodName, result);
    }

    // Log execution time for services
    @Around("execution(* com.java.moveminds.services..*(..))")
    public Object logServiceExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        log.debug("▶ Starting service: {}", methodName);

        Object result = joinPoint.proceed();

        long timeTaken = System.currentTimeMillis() - startTime;
        log.debug("⏱ Finished service: {} in {} ms", methodName, timeTaken);
        return result;
    }
}
