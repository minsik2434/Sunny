package com.sunny.projectservice.client;

import com.sunny.projectservice.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/user/{userEmail}")
    UserResponseDto getUser(@PathVariable String userEmail);
}
