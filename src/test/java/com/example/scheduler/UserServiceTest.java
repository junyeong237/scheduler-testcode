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
import static org.junit.jupiter.api.Assertions.*;
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
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("park","123456789");
        given(userRepository.findByUsername(signUpRequestDto.getUsername()))
                .willReturn(Optional.empty());
        given(passwordEncoder.encode(signUpRequestDto.getPassword())).willReturn("encodedPassword");


        //When
        userService.signup(signUpRequestDto);

        //Then
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));

    }

    @Test
    @DisplayName("User 회원가입 테스트 실패 중복된 아이디 ")

    public void testSignupFail(){

        //Given
        User newUSer = new User();
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("park","123456789");
        given(userRepository.findByUsername(signUpRequestDto.getUsername()))
                .willReturn(Optional.of(newUSer));
        given(passwordEncoder.encode(signUpRequestDto.getPassword())).willReturn("encodedPassword");


        //When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.signup(signUpRequestDto);
        });



        //Then
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class)); // save가 호출되면 안되는걸 검증
        assertEquals(
                "중복된 사용자가 존재합니다.",
                exception.getMessage()
        );

    }

}
