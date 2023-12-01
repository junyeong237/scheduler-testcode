package com.example.scheduler;

import com.example.scheduler.dto.PostRequestDto;
import com.example.scheduler.dto.SignUpRequestDto;
import com.example.scheduler.entity.User;
import com.example.scheduler.repository.UserRepository;
import com.example.scheduler.service.PostService;
import com.example.scheduler.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


    @Test
    @DisplayName("User 회원가입 테스트 성공 ")

    public void testSignup(){

        //Given
        Long userId = 100L;
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("park","123456789");
        given(userRepository.findByUsername(signUpRequestDto.getUsername()))
                .willReturn(Optional.empty());
        given(passwordEncoder.encode(signUpRequestDto.getPassword())).willReturn("encodedPassword");


        //When
        userService.signup(signUpRequestDto);

        //Then
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));

    }



}
