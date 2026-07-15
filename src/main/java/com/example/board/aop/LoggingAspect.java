package com.example.board.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    @Around("execution(* com.example.board.service..*(..))")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();;
        long start = System.currentTimeMillis();

        log.debug("> {} 시작", method);
        Object result = joinPoint.proceed();
        long time = System.currentTimeMillis() - start;
        log.debug("< {} 완료 ({}ms)", method, time);

        return result;
    }
}
