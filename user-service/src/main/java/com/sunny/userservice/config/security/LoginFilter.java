//package com.sunny.userservice.config.security;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sunny.userservice.dto.LoginRequestDto;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
//@Slf4j
//public class LoginFilter extends UsernamePasswordAuthenticationFilter {
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
//            throws AuthenticationException {
//        try {
//            LoginRequestDto loginInfo = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);
//            UsernamePasswordAuthenticationToken authenticationToken =
//                    new UsernamePasswordAuthenticationToken(loginInfo.getEmail(),
//                            loginInfo.getPassword(), new ArrayList<GrantedAuthority>());
//
//            return getAuthenticationManager().authenticate(authenticationToken);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
