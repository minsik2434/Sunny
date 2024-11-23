package com.sunny.userservice.config.security.handler;

import com.sunny.userservice.config.security.JwtProvider;
import com.sunny.userservice.dto.TokenResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class LoginSuccessHandlerTest {

    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Authentication authentication;
    @Mock
    private User user;
    @InjectMocks
    private LoginSuccessHandler loginSuccessHandler;

    @Test
    void onAuthenticationSuccessTest() throws IOException, ServletException {
        //사용자 이메일
        String email = "testEmail@naver.com";
        //JWT 생성
        TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken").build();

        //authentication 목 객체의 getPrincipal() 반환값으로 목 User 반환하도록 설정
        when(authentication.getPrincipal()).thenReturn(user);
        //목 User의 getUsername() 반환값으로 사용자 이메일 반환하도록 설정
        when(user.getUsername()).thenReturn(email);
        //jwtProvider.createToken(email) 반환값으로 생성한 JWT 반환하도록 설정
        when(jwtProvider.createToken(email)).thenReturn(tokenResponseDto);

        //PrintWriter가 StringWriter애 응답 값을 저장하도록 설정
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        //response Writer를 생성한 printWriter를 사용하도록 설정
        when(response.getWriter()).thenReturn(printWriter);

        //loginSuccessHandler 로직 실행
        loginSuccessHandler.onAuthenticationSuccess(null, response, authentication);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        //응답 바디
        String responseBody = stringWriter.toString();
        assertThat(responseBody).contains("accessToken");
        assertThat(responseBody).contains("refreshToken");
    }

}