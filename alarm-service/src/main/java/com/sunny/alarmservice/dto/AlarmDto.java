package com.sunny.alarmservice.dto;

import com.sunny.alarmservice.domain.Alarm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmDto {

    private Long senderProjectId;
    private String type;
    private String content;

    public AlarmDto(Alarm alarm){
        this.senderProjectId = alarm.getSender();
        this.type = alarm.getType();
        this.content = alarm.getContent();
    }
}
