package com.example.scheduler.service;

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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

        User user = new User();
        user.setId(userId);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        PostRequestDto postRequestDto = new PostRequestDto("제목1","내용1");
        //When
        PostResponseDto result = postService.create(postRequestDto, userDetails);
        // Then
        assertEquals("내용1", result.getContent());
        Mockito.verify(postRepository, Mockito.times(1)).save(any(PostEntity.class));

    }

    @Test
    @DisplayName("Post 수정 테스트")
    public void testUpdate() {

        // Given
        Long userId = 100L;
        User user = new User();
        user.setId(userId);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));



        Long postId = 100L;
        PostEntity post = PostEntity.builder()
                .Id(postId)
                .user(user)
                .finished(false)
                .title("원래 제목입니다.")
                .content("원래 내용입니다.")
                .build();

        PostRequestDto postRequestDto = new PostRequestDto("수정한 제목", "수정한 내용");


        given(postRepository.findByUserAndId(user,postId)).willReturn(Optional.of(post));

        // When
        PostResponseDto result = postService.updatePost(postId,postRequestDto, userDetails);

        // Then
        assertEquals("수정한 내용", result.getContent());

    }

    @Test
    @DisplayName("Post 수정 테스트 실패")
    public void testUpdateFail() {

        // Given
        Long userId = 100L;
        User user = new User();
        user.setId(userId);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));



        Long postId = 100L;
        PostEntity post = PostEntity.builder()
                .Id(postId)
                .user(user)
                .finished(false)
                .title("원래 제목입니다.")
                .content("원래 내용입니다.")
                .build();

        PostRequestDto postRequestDto = new PostRequestDto("수정한 제목", "수정한 내용");


        given(postRepository.findByUserAndId(user,postId)).willReturn(Optional.empty());

        // When
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> postService.updatePost(postId,postRequestDto, userDetails));

        // Then
        assertEquals(
                "알맞는 사용자가 아니거나 해당 게시글을 찾을 수 없습니다.",
                exception.getMessage()
        );

    }

    @Test
    @DisplayName("단일 Post 가져오기")
    public void testGetPost() {
        // Given
        Long userId = 100L;
        User user = new User();
        user.setId(userId);


        Long postId = 100L;
        PostEntity post = PostEntity.builder()
                .Id(postId)
                .user(user)
                .finished(false)
                .title("원래 제목입니다.")
                .content("원래 내용입니다.")
                .build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));



        // When
        PostResponseDto result = postService.getPost(postId);

        // Then
        assertEquals("원래 내용입니다.", result.getContent());


    }



}
