package com.sunny.userservice.service;

import com.sunny.userservice.client.ProjectClient;
import com.sunny.userservice.common.JwtProvider;
import com.sunny.userservice.common.exception.CredentialException;
import com.sunny.userservice.common.exception.DuplicateResourceException;
import com.sunny.userservice.common.exception.ResourceNotFoundException;
import com.sunny.userservice.domain.Member;
import com.sunny.userservice.dto.*;
import com.sunny.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String,String> redisTemplate;
    private final ProjectClient projectClient;

    @Value("${jwt.refresh-token-expiration}")
    private long refresh_token_expirationTime;

    @Transactional
    @Override
    public void save(UserRequestDto userRequestDto) {
        if(userRepository.findByEmail(userRequestDto.getEmail()).isPresent()){
            throw new DuplicateResourceException("This email already exists");
        }
        String encryptPassword = passwordEncoder.encode(userRequestDto.getPassword());
        Member member = new Member(userRequestDto,encryptPassword);
        userRepository.save(member);
    }

    @Override
    public UserResponseDto getUser(String email) {
        Optional<Member> optional = userRepository.findByEmail(email);
        if(optional.isEmpty()){
            throw new ResourceNotFoundException("User Not Found");
        }
        Member member = optional.get();
        List<ProjectResponseDto> projectList = projectClient.getProjectList(member.getEmail());
        return UserResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .profileUrl(member.getProfileUrl())
                .projects(projectList).build();
    }


    @Override
    public TokenResponseDto login(LoginRequestDto loginRequestDto) {
        Optional<Member> user = userRepository.findByEmail(loginRequestDto.getEmail());
        if(user.isEmpty()){
            throw new CredentialException("Email or Password Incorrect");
        }
        Member member = user.get();
        String email = member.getEmail();
        String encryptPassword = member.getPassword();

        if(!passwordEncoder.matches(loginRequestDto.getPassword(),encryptPassword)){
            throw new CredentialException("Email or Password Incorrect");
        }
        TokenResponseDto token = jwtProvider.createToken(email);
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        vop.set(email, token.getRefreshToken(), refresh_token_expirationTime, TimeUnit.MILLISECONDS);
        return token;
    }

    @Override
    public void logout(String email) {
        redisTemplate.delete(email);
    }

    @Override
    public boolean exist(String userEmail) {
        Optional<Member> user = userRepository.findByEmail(userEmail);
        return user.isPresent();
    }

    @Override
    public TokenResponseDto refresh(String refreshToken) {
        String email = jwtProvider.getClaim(refreshToken);
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        String savedRefreshToken = vop.get(email);
        if(savedRefreshToken == null){
            throw new ResourceNotFoundException("RefreshToken Not Found");
        }
        else if(!refreshToken.equals(savedRefreshToken)){
            throw new CredentialException("RefreshToken Not Matched");
        }
        TokenResponseDto token = jwtProvider.createToken(email);
        vop.set(email,token.getRefreshToken());
        return token;
    }
}
