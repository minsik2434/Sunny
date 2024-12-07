package com.sunny.taskservice.domain;

import jakarta.persistence.*;

@Entity
public class TaskMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_task_id")
    private SubTask subTask;
}
