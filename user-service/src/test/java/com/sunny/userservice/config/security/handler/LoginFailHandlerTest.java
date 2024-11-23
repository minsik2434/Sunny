package com.sunny.userservice.config.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginFailHandlerTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @InjectMocks
    private LoginFailHandler loginFailHandler;

    @Test
    @DisplayName("로그인 실패시 응답 테스트")
    void onAuthenticationFailureTest() throws ServletException, IOException {
        BadCredentialsException exception = new BadCredentialsException("Invalid Email or Password");
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(request.getRequestURI()).thenReturn("/login");
        when(response.getWriter()).thenReturn(printWriter);

        loginFailHandler.onAuthenticationFailure(request, response, exception);

        String responseBody = stringWriter.toString();
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        assertThat(responseBody).contains("Login Fail");
        assertThat(responseBody).contains("Invalid Username or Password");
        assertThat(responseBody).contains("/login");

    }
}