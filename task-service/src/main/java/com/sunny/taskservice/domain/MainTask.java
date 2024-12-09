package com.sunny.taskservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MainTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime deadline;
    private Long projectId;

    public MainTask(String name, String description, LocalDateTime startDate, LocalDateTime deadLine, Long projectId){
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.deadline = deadLine;
        this.projectId = projectId;
    }

}
