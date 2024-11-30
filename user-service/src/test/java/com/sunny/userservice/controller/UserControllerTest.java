package com.sunny.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunny.userservice.common.JwtProvider;
import com.sunny.userservice.common.exception.CredentialException;
import com.sunny.userservice.common.exception.DuplicateResourceException;
import com.sunny.userservice.common.exception.ResourceNotFoundException;
import com.sunny.userservice.dto.LoginRequestDto;
import com.sunny.userservice.dto.TokenResponseDto;
import com.sunny.userservice.dto.UserRequestDto;
import com.sunny.userservice.dto.UserResponseDto;
import com.sunny.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    ObjectMapper mapper = new ObjectMapper();
    UserRequestDto userRequestDto;
    LoginRequestDto loginRequestDto;
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    JwtProvider jwtProvider;
    @BeforeEach
    void userInit(){
        userRequestDto = new UserRequestDto(
                "testEmail@gmail.com",
                "tesPassword",
                "user1",
                "010-2434-4402");
        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("testEmail");
        loginRequestDto.setPassword("testPassword");
    }
    @Test
    @DisplayName("회원 저장")
    void createUserTest() throws Exception {
        doNothing().when(userService).save(any(UserRequestDto.class));
        String requestBody = mapper.writeValueAsString(userRequestDto);
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("회원 저장 - 동일한 이메일 존재시")
    void createUserTest_DuplicateEmail() throws Exception {
        doThrow(new DuplicateResourceException("This is a duplicate resource"))
                .when(userService).save(any(UserRequestDto.class));


        String requestBody = mapper.writeValueAsString(userRequestDto);
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Duplicate"))
                .andExpect(jsonPath("$.message").value("This is a duplicate resource"))
                .andExpect(jsonPath("$.path").value("/user"));
    }

    @Test
    @DisplayName("회원 정보 조회")
    void userInfoTest() throws Exception {
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .id(1L)
                .email("testEmail@naver.com")
                .name("tester")
                .phoneNumber("010-1234-5678")
                .profileUrl("testProfileImageUrl").build();

        when(userService.getUser(any(String.class))).thenReturn(userResponseDto);

        mockMvc.perform(get("/user/email"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDto.getId()))
                .andExpect(jsonPath("$.email").value(userResponseDto.getEmail()))
                .andExpect(jsonPath("$.name").value(userResponseDto.getName()))
                .andExpect(jsonPath("$.phoneNumber").value(userResponseDto.getPhoneNumber()))
                .andExpect(jsonPath("$.profileUrl").value(userResponseDto.getProfileUrl()));
    }

    @Test
    @DisplayName("회원 정보 조회 - 존재하지 않는 회원인 경우")
    void userInfoTest_notFound() throws Exception {
        doThrow(new ResourceNotFoundException("user not found")).when(userService).getUser(any(String.class));

        mockMvc.perform(get("/user/email"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("user not found"))
                .andExpect(jsonPath("$.path").value("/user/email"));
    }

    @Test
    @DisplayName("회원 로그인")
    void loginTest() throws Exception {
        String requestBody = mapper.writeValueAsString(loginRequestDto);
        TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                                .accessToken("accessToken")
                                .refreshToken("refreshToken")
                                .build();

        when(jwtProvider.createToken(anyString())).thenReturn(tokenResponseDto);
        when(userService.login(any(LoginRequestDto.class))).thenReturn(tokenResponseDto);

        mockMvc.perform(post("/login")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(tokenResponseDto.getAccessToken()))
                .andExpect(jsonPath("$.refreshToken").value(tokenResponseDto.getRefreshToken()));
    }

    @Test
    @DisplayName("회원 로그인 - 로그인 실패")
    void loginTest_fail() throws Exception {
        String requestBody = mapper.writeValueAsString(loginRequestDto);
        doThrow(new CredentialException("Email or Password Incorrect"))
                .when(userService).login(any(LoginRequestDto.class));

        mockMvc.perform(post("/login")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UnAuthorization"))
                .andExpect(jsonPath("$.message").value("Email or Password Incorrect"))
                .andExpect(jsonPath("$.path").value("/login"));
    }

    @Test
    @DisplayName("회원 로그아웃")
    void logoutTest() throws Exception {
        doNothing().when(userService).logout(anyString());

        mockMvc.perform(get("/logout/email"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("리프레시")
    void refreshTest() throws Exception {
        TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                .accessToken("access")
                .refreshToken("refresh").build();

        when(userService.refresh(anyString())).thenReturn(tokenResponseDto);

        mockMvc.perform(post("/refresh")
                .contentType("application/json")
                .header("Authorization", "Bearer testToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"));
    }

    @Test
    @DisplayName("리프레시 - 리프레시 토큰이 일치하지 않을때")
    void refreshTest_notMatch() throws Exception {
        doThrow(new CredentialException("RefreshToken Not Matched")).when(userService).refresh(anyString());

        mockMvc.perform(post("/refresh")
                .contentType("application/json")
                .header("Authorization","Bearer testToken"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UnAuthorization"))
                .andExpect(jsonPath("$.message").value("RefreshToken Not Matched"))
                .andExpect(jsonPath("$.path").value("/refresh"));
    }

    @Test
    @DisplayName("리프레시 - 리프레시 토큰을 찾을 수 없을때")
    void refreshTest_noData() throws Exception {
        doThrow(new ResourceNotFoundException("RefreshToken Not Found")).when(userService).refresh(anyString());

        mockMvc.perform(post("/refresh")
                        .contentType("application/json")
                        .header("Authorization","Bearer testToken"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("RefreshToken Not Found"))
                .andExpect(jsonPath("$.path").value("/refresh"));
    }

}