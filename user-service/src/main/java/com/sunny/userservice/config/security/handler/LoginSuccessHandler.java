//package com.sunny.userservice.config.security.handler;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sunny.userservice.common.JwtProvider;
//import com.sunny.userservice.dto.TokenResponseDto;
//import io.jsonwebtoken.Claims;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//
//import java.io.IOException;
//
//@Slf4j
//@RequiredArgsConstructor
//public class LoginSuccessHandler implements AuthenticationSuccessHandler {
//    private final JwtProvider jwtProvider;
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request,
//                                        HttpServletResponse response,
//                                        Authentication authentication) throws IOException, ServletException {
//        ObjectMapper mapper = new ObjectMapper();
//        String email = ((User) authentication.getPrincipal()).getUsername();
//        TokenResponseDto token = jwtProvider.createToken(email);
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.setStatus(HttpServletResponse.SC_OK);
//        response.getWriter().write(mapper.writeValueAsString(token));
//    }
//}
