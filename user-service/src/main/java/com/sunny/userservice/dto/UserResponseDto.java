package com.sunny.userservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class UserResponseDto {
    private Long id;
    private String email;
    private String name;
    private String phoneNumber;
    private String profileUrl;
    private List<ProjectResponseDto> projects;
}
