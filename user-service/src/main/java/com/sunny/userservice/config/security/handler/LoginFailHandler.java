//package com.sunny.userservice.config.security.handler;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sunny.userservice.dto.FailResponse;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.AuthenticationFailureHandler;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//
//@Slf4j
//public class LoginFailHandler implements AuthenticationFailureHandler {
//
//    @Override
//    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//        ObjectMapper mapper = new ObjectMapper();
//        FailResponse failResponse = FailResponse.builder()
//                .status(HttpStatus.UNAUTHORIZED)
//                .error("Login Fail")
//                .message("Invalid Username or Password")
//                .dateTime(dateFormat.format(System.currentTimeMillis()))
//                .path(request.getRequestURI()).build();
//
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.getWriter().write(mapper.writeValueAsString(failResponse));
//    }
//}
