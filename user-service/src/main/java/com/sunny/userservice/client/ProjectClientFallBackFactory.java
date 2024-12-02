package com.sunny.userservice.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class ProjectClientFallBackFactory implements FallbackFactory<ProjectClient> {
    @Override
    public ProjectClient create(Throwable cause) {
        return userEmail -> {
            // 로그를 남기거나 원인을 추적 가능
            System.err.println("Fallback triggered due to: " + cause.getMessage());
            return Collections.emptyList();
        };
    }
}
