package com.sunny.projectservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private Long id;
    private String email;
    private String name;
    private String phoneNumber;
    private String profileUrl;
}
