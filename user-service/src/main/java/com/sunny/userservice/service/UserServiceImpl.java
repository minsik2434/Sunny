package com.sunny.userservice.service;

import com.sunny.userservice.common.JwtProvider;
import com.sunny.userservice.common.exception.CredentialException;
import com.sunny.userservice.common.exception.DuplicateResourceException;
import com.sunny.userservice.common.exception.ResourceNotFoundException;
import com.sunny.userservice.domain.Member;
import com.sunny.userservice.dto.LoginRequestDto;
import com.sunny.userservice.dto.TokenResponseDto;
import com.sunny.userservice.dto.UserRequestDto;
import com.sunny.userservice.dto.UserResponseDto;
import com.sunny.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

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

        return UserResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .profileUrl(member.getProfileUrl()).build();

    }

//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        Optional<Member> optional = userRepository.findByEmail(email);
//        if(optional.isEmpty()){
//            throw new UsernameNotFoundException("User Not Found");
//        }
//        Member member = optional.get();
//        return User.builder().username(member.getEmail())
//                .password(member.getPassword())
//                .roles("USER").build();
//    }


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

        return jwtProvider.createToken(email);
    }
}
