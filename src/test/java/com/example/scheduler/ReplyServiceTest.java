package com.example.scheduler;

import com.example.scheduler.dto.ReplyRequestDto;
import com.example.scheduler.dto.ReplyResponseDto;
import com.example.scheduler.entity.PostEntity;
import com.example.scheduler.entity.ReplyEntity;
import com.example.scheduler.entity.User;
import com.example.scheduler.repository.PostRepository;
import com.example.scheduler.repository.ReplyRepository;
import com.example.scheduler.repository.UserRepository;
import com.example.scheduler.security.UserDetailsImpl;
import com.example.scheduler.service.ReplyService;
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
public class ReplyServiceTest {


    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReplyRepository replyRepository;

    @InjectMocks
    private ReplyService replyService;

    @Test
    @DisplayName("댓글 생성 테스트")
    void testCreate() {
        // Given
        ReplyRequestDto replyRequestDto = new ReplyRequestDto("댓글작성");
        Long postId = 1L;


        PostEntity postEntity = new PostEntity();
        postEntity.setId(postId);

        User user = new User();
        user.setId(1L);
        user.setUsername("park");
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        given(postRepository.findById(postId)).willReturn(Optional.of(postEntity));
        given(userRepository.findById(userDetails.getUser().getId())).willReturn(Optional.of(user));


        // When
        ReplyResponseDto createdReply = replyService.create(replyRequestDto, postId, userDetails);

        // Then
        assertEquals("댓글작성", createdReply.getContent());
        Mockito.verify(replyRepository, Mockito.times(1)).save(any(ReplyEntity.class));

    }


    @Test
    @DisplayName("댓글 수정 테스트")
    void testUpdate() {
        // Given
        Long replyId = 1L;
        ReplyRequestDto replyRequestDto = new ReplyRequestDto("댓글수정입니다.");


        User user = new User();
        user.setId(1L);
        user.setUsername("leee");
        UserDetailsImpl userDetails = new UserDetailsImpl(new User());

        ReplyEntity replyEntity = new ReplyEntity();
        replyEntity.setId(replyId);
        replyEntity.setUser(user);
        replyEntity.setPost(new PostEntity());
        replyEntity.setContent("원래 댓글내용입니다.");

        given(userRepository.findById(userDetails.getUser().getId())).willReturn(Optional.of(user));
        given(replyRepository.findByUserAndId(user, replyId)).willReturn(Optional.of(replyEntity));


        // When
        ReplyResponseDto updatedReply = replyService.updateReply(replyId, replyRequestDto, userDetails);

        // Then
        assertEquals("댓글수정입니다.", updatedReply.getContent());
    }

    @Test
    @DisplayName("댓글 단일 조회 테스트")
    void testGet() {
        // Given
        Long replyId = 1L;

        User user = new User();
        user.setId(1L);
        user.setUsername("hwang");
        UserDetailsImpl userDetails = new UserDetailsImpl(new User());


        ReplyEntity replyEntity = new ReplyEntity();
        replyEntity.setId(replyId);
        replyEntity.setUser(user);
        replyEntity.setContent("조회할댓글내용입니다.");

        given(userRepository.findById(userDetails.getUser().getId())).willReturn(Optional.of(user));
        given(replyRepository.findByUserAndId(user, replyId)).willReturn(Optional.of(replyEntity));

        // When
        ReplyEntity Reply = replyService.getReply(replyId, userDetails);

        // Then
        assertEquals("조회할댓글내용입니다.", Reply.getContent());
    }


}