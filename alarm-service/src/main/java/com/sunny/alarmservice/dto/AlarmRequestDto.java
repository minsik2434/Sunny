package com.sunny.alarmservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmRequestDto {
    private String type;
    private String recipientEmail;
    private Long projectId;
    private String content;
}
