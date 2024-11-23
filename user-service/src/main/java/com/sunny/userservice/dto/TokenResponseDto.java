package com.sunny.userservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
}
