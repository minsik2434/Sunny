package com.sunny.taskservice.dto;

import com.sunny.taskservice.common.annotation.validation.DateTimePattern;
import com.sunny.taskservice.common.annotation.validation.StartDateTimeDeadLineValid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@StartDateTimeDeadLineValid(startDateTime = "startDateTime", deadLine = "deadLine")
public class CreateMainTaskRequestDto {
    private Long projectId;
    @NotBlank
    private String name;
    private String description;
    @DateTimePattern
    private String startDateTime;
    @DateTimePattern
    private String deadLine;
}
