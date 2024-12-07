package com.sunny.taskservice.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CreateMainTaskRequestDto {
    private String name;
    private String description;

    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$",
            message = "startDateTime must be in the format yyyy-MM-dd HH:mm:ss"
    )
    private String startDateTime;
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$",
            message = "Deadline must be in the format yyyy-MM-dd HH:mm:ss"
    )
    private String deadLine;
}
