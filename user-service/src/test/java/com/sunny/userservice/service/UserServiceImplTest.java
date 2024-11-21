package com.sunny.userservice.service;

import com.sunny.userservice.common.exception.DuplicateResourceException;
import com.sunny.userservice.common.exception.ResourceNotFoundException;
import com.sunny.userservice.domain.Member;
import com.sunny.userservice.dto.UserRequestDto;
import com.sunny.userservice.dto.UserResponseDto;
import com.sunny.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Slf4j
class UserServiceImplTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    UserRequestDto userRequestDto;
    @BeforeEach
    void userInit(){
        userRequestDto = new UserRequestDto(
                "testEmail@gmail.com",
                "tesPassword",
                "user1",
                "010-2434-4402");
    }

    @AfterEach
    void initDB(){
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 저장 테스트")
    void saveTest(){
        userService.save(userRequestDto);
        Member findMember = userRepository.findByEmail(userRequestDto.getEmail()).get();

        checkSaveMember(findMember);
    }

    @Test
    @DisplayName("회원 저장 테스트 - 이메일 중복")
    void saveTest_DuplicateEmail(){
        userService.save(userRequestDto);
        assertThatThrownBy(()->userService.save(userRequestDto)).isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("회원 조회 테스트")
    void getUserTest(){
        userService.save(userRequestDto);

        Member member = userRepository.findByEmail(userRequestDto.getEmail()).get();
        UserResponseDto userResponseDto = userService.getUser(userRequestDto.getEmail());

        assertThat(member.getId()).isEqualTo(userResponseDto.getId());
        assertThat(member.getEmail()).isEqualTo(userResponseDto.getEmail());
        assertThat(member.getName()).isEqualTo(userResponseDto.getName());
        assertThat(member.getPhoneNumber()).isEqualTo(userResponseDto.getPhoneNumber());
        assertThat(member.getProfileUrl()).isEqualTo(userResponseDto.getProfileUrl());

    }

    @Test
    @DisplayName("회원 조회 테스트 - 존재하지 않는 회원")
    void getUserTest_NotFound(){
        assertThatThrownBy(()-> userService.getUser(userRequestDto.getEmail()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private void checkSaveMember(Member member){
        assertThat(member.getEmail()).isEqualTo(userRequestDto.getEmail());
        assertThat(member.getName()).isEqualTo(userRequestDto.getName());
        assertThat(member.getPhoneNumber()).isEqualTo(userRequestDto.getPhoneNumber());
        assertThat(passwordEncoder.matches(userRequestDto.getPassword(), member.getPassword())).isTrue();
    }
}