package com.sunny.userservice.domain;

import com.sunny.userservice.dto.UserRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String password;
    private String name;

    @Column(unique = true)
    private String phoneNumber;
    private String profileUrl;

    public Member(UserRequestDto userRequestDto,String encryptPassword){
        this.email = userRequestDto.getEmail();
        this.password = encryptPassword;
        this.name = userRequestDto.getName();
        this.phoneNumber = userRequestDto.getPhoneNumber();
        this.profileUrl = "123";
    }
}
