package com.sunny.taskservice.common.client.circuitbreaker;

import feign.Target;
import org.springframework.cloud.openfeign.CircuitBreakerNameResolver;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class NameResolver implements CircuitBreakerNameResolver {
    @Override
    public String resolveCircuitBreakerName(String feignClientName, Target<?> target, Method method) {
        return feignClientName;
    }
}
