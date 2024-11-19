package com.sunny.userservice.service;

import com.sunny.userservice.domain.Member;
import com.sunny.userservice.domain.dto.UserRequestDto;
import com.sunny.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void save(UserRequestDto userRequestDto) {
        String encryptPassword = passwordEncoder.encode(userRequestDto.getPassword());
        Member member = new Member(userRequestDto,encryptPassword);
        userRepository.save(member);
    }
}
