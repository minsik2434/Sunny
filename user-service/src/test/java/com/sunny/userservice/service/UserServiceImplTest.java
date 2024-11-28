package com.sunny.userservice.service;

import com.sunny.userservice.common.exception.CredentialException;
import com.sunny.userservice.common.exception.DuplicateResourceException;
import com.sunny.userservice.common.exception.ResourceNotFoundException;
import com.sunny.userservice.domain.Member;
import com.sunny.userservice.dto.LoginRequestDto;
import com.sunny.userservice.dto.TokenResponseDto;
import com.sunny.userservice.dto.UserRequestDto;
import com.sunny.userservice.dto.UserResponseDto;
import com.sunny.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Slf4j
class UserServiceImplTest extends TestContainerConfig{

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    UserRequestDto userRequestDto;
    LoginRequestDto loginRequestDto;
    @BeforeEach
    void userInit(){
        userRequestDto = new UserRequestDto(
                "testEmail@gmail.com",
                "testPassword",
                "user1",
                "010-2434-4402");

        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("testEmail@gmail.com");
        loginRequestDto.setPassword("testPassword");
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

    @Test
    @DisplayName("회원 로그인 테스트")
    void loginTest(){
        userService.save(userRequestDto);
        TokenResponseDto token = userService.login(loginRequestDto);

        assertThat(token.getAccessToken()).isNotNull();
        assertThat(token.getAccessToken()).isInstanceOf(String.class);

        assertThat(token.getRefreshToken()).isNotNull();
        assertThat(token.getRefreshToken()).isInstanceOf(String.class);

        ValueOperations<String, String> vop = redisTemplate.opsForValue();

        String savedToken = vop.get(userRequestDto.getEmail());

        assertThat(savedToken).isNotNull();
        assertThat(savedToken).isEqualTo(token.getRefreshToken());
    }

    @Test
    @DisplayName("로그인 실패")
    void loginTest_fail(){
        userService.save(userRequestDto);
        LoginRequestDto incorrectLogin = new LoginRequestDto();
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        incorrectLogin.setEmail("incorrectEmail");
        incorrectLogin.setPassword("incorrectPassword");
        assertThatThrownBy(()-> userService.login(incorrectLogin)).isInstanceOf(CredentialException.class);
        assertThat(vop.get(incorrectLogin.getEmail())).isNull();
    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logoutTest(){
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        userService.save(userRequestDto);
        userService.login(loginRequestDto);

        userService.logout(loginRequestDto.getEmail());

        assertThat(vop.get(loginRequestDto.getEmail())).isNull();
    }

    @Test
    @DisplayName("리프레시 테스트")
    void refreshTest(){
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        userService.save(userRequestDto);
        TokenResponseDto login = userService.login(loginRequestDto);

        TokenResponseDto refresh = userService.refresh(login.getRefreshToken());

        assertThat(refresh.getAccessToken()).isNotNull();
        assertThat(refresh.getRefreshToken()).isNotNull();

        assertThat(refresh.getAccessToken()).isInstanceOf(String.class);
        assertThat(refresh.getRefreshToken()).isInstanceOf(String.class);

        assertThat(vop.get(loginRequestDto.getEmail())).isEqualTo(refresh.getRefreshToken());
    }


    private void checkSaveMember(Member member){
        assertThat(member.getEmail()).isEqualTo(userRequestDto.getEmail());
        assertThat(member.getName()).isEqualTo(userRequestDto.getName());
        assertThat(member.getPhoneNumber()).isEqualTo(userRequestDto.getPhoneNumber());
        assertThat(passwordEncoder.matches(userRequestDto.getPassword(), member.getPassword())).isTrue();
    }
}