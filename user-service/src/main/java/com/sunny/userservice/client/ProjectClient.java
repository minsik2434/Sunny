package com.sunny.userservice.client;

import com.sunny.userservice.dto.ProjectResponseDto;
//import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "project-service", url = "${feign-endpoint}",fallbackFactory = ProjectClientFallBackFactory.class)
public interface ProjectClient {
    @GetMapping("/project/{userEmail}")
    List<ProjectResponseDto> getProjectList(@PathVariable String userEmail);
}
