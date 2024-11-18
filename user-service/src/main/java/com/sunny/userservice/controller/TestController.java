package com.sunny.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final Environment env;

    @GetMapping("/test")
    public String test(){
        String property = env.getProperty("test.value");
        return property;
    }
}
