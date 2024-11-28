//package com.sunny.userservice.config.security;
//
//import com.sunny.userservice.config.security.handler.LoginFailHandler;
//import com.sunny.userservice.config.security.handler.LoginSuccessHandler;
//import com.sunny.userservice.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.crypto.factory.PasswordEncoderFactories;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.AuthenticationFailureHandler;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final UserService userService;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtProvider jwtProvider;
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        AuthenticationManagerBuilder authenticationManagerBuilder =
//                http.getSharedObject(AuthenticationManagerBuilder.class);
//        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder);
//        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
//
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .httpBasic(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
//                .authenticationManager(authenticationManager)
//                .addFilter(loginFilter(authenticationManager));
//        return http.build();
//    }
//
//    private LoginFilter loginFilter(AuthenticationManager authenticationManager){
//        LoginFilter loginFilter = new LoginFilter();
//        loginFilter.setAuthenticationManager(authenticationManager);
//        loginFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
//        loginFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
//        return loginFilter;
//    }
//
//    @Bean
//    public AuthenticationFailureHandler authenticationFailureHandler(){
//        return new LoginFailHandler();
//    }
//
//    @Bean
//    public AuthenticationSuccessHandler authenticationSuccessHandler(){
//        return new LoginSuccessHandler(jwtProvider);
//    }
//}
