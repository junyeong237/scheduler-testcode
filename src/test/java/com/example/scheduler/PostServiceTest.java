package com.example.scheduler;

import com.example.scheduler.dto.PostRequestDto;
import com.example.scheduler.dto.PostResponseDto;
import com.example.scheduler.entity.PostEntity;
import com.example.scheduler.entity.User;
import com.example.scheduler.repository.PostRepository;
import com.example.scheduler.repository.UserRepository;
import com.example.scheduler.security.UserDetailsImpl;
import com.example.scheduler.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("Post 생성 테스트")
    public void testCreate() {
        // Given
        Long userId = 100L;
        PostRequestDto postRequestDto = new PostRequestDto("제목1","내용1");
        User user = new User();
        user.setId(userId);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        //When
        PostResponseDto result = postService.create(postRequestDto, userDetails);
        // Then
        assertEquals("내용1", result.getContent());

    }






}
