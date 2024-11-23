package com.sunny.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunny.userservice.common.exception.DuplicateResourceException;
import com.sunny.userservice.common.exception.ResourceNotFoundException;
import com.sunny.userservice.dto.UserRequestDto;
import com.sunny.userservice.dto.UserResponseDto;
import com.sunny.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @TestConfiguration
    static class SecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }
    }
    UserRequestDto userRequestDto;
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;
    @BeforeEach
    void userInit(){
        userRequestDto = new UserRequestDto(
                "testEmail@gmail.com",
                "tesPassword",
                "user1",
                "010-2434-4402");
    }
    @Test
    @DisplayName("회원 저장")
    void createUserTest() throws Exception {
        doNothing().when(userService).save(any(UserRequestDto.class));
        ObjectMapper mapper = new ObjectMapper();
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

        ObjectMapper mapper = new ObjectMapper();
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
    @DisplayName("회원 로그인 - 성공")
    void loginTest(){

    }

    @Test
    @DisplayName("회원 로그인 - 실패")
    void loginTest_Fail(){

    }
}