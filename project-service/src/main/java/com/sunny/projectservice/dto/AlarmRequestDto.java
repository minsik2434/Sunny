package com.sunny.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AlarmRequestDto {
    private String type;
    private String recipientEmail;
    private Long projectId;
    private String content;
}
