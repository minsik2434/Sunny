package com.sunny.userservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponseDto {
    private Long id;
    private String email;
    private String name;
    private String phoneNumber;
    private String profileUrl;
}
