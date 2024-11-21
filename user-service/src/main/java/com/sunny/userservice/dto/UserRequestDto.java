package com.sunny.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRequestDto {

    @Email
    private String email;
    @NotBlank
    private String password;
    @NotNull
    private String name;
    @NotNull
    private String phoneNumber;
}
