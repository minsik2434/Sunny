package com.sunny.alarmservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private Long sender;
    private String recipientEmail;
    private String content;
    private Date alarmTime;

    public Alarm(String type, Long sender, String recipientEmail, String content){
        this.type = type;
        this.sender = sender;
        this.recipientEmail = recipientEmail;
        this.content = content;
        this.alarmTime = new Date(System.currentTimeMillis());
    }
}
