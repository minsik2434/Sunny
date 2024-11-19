package com.sunny.userservice.service;

import com.sunny.userservice.domain.Member;
import com.sunny.userservice.domain.dto.UserRequestDto;
import com.sunny.userservice.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class MemberServiceImplTest {

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
    @Test
    @DisplayName("회원 저장 테스트")
    void saveTest(){
        userService.save(userRequestDto);
        Member findMember = userRepository.findByEmail(userRequestDto.getEmail());

        checkSaveMember(findMember);
    }

    private void checkSaveMember(Member member){
        assertThat(member.getEmail()).isEqualTo(userRequestDto.getEmail());
        assertThat(member.getName()).isEqualTo(userRequestDto.getName());
        assertThat(member.getPhoneNumber()).isEqualTo(userRequestDto.getPhoneNumber());
        assertThat(passwordEncoder.matches(userRequestDto.getPassword(), member.getPassword())).isTrue();
    }
}