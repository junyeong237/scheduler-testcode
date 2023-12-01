package com.example.scheduler.service;

import com.example.scheduler.dto.PostResponseDto;
import com.example.scheduler.dto.SignUpRequestDto;
import com.example.scheduler.dto.UserResponseDto;
import com.example.scheduler.entity.User;
import com.example.scheduler.entity.UserRoleEnum;
import com.example.scheduler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ADMIN_TOKEN

    public void signup(SignUpRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // 사용자 등록
        User user = User.builder()
                .username(username) // 11/16 18:56
                .password(password)
                .postList(Collections.emptyList())
                .replylist(Collections.emptyList())
                .build();

        userRepository.save(user);
    }

    public List<UserResponseDto> getAllUser() {

        List<User> userList = userRepository.findAll();

        if(userList.isEmpty()){
            return Collections.emptyList();
        }

        return userList
                .stream()
                .map(UserResponseDto::new)
                .toList();


    }
}
