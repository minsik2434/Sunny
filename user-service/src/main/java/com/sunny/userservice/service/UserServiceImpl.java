package com.sunny.userservice.service;

import com.sunny.userservice.common.exception.DuplicateResourceException;
import com.sunny.userservice.domain.Member;
import com.sunny.userservice.dto.UserRequestDto;
import com.sunny.userservice.dto.UserResponseDto;
import com.sunny.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
    public UserResponseDto getUser(String userId) {
        return null;
    }
}
