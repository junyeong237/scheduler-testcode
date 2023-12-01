package com.example.scheduler;

import com.example.scheduler.dto.*;
import com.example.scheduler.entity.PostEntity;
import com.example.scheduler.entity.ReplyEntity;
import com.example.scheduler.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class EntityTest {
    @Test
    @DisplayName("PostEntity 테스트")
    public void testPost() {
        // given
        PostEntity postEntity = new PostEntity();
        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitle("테스트용 타이틀1");
        postRequestDto.setContent("테스트용 내용1");

        // when
        postEntity = PostEntity.builder()
                .Id(1L)
                .finished(false)
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .build();


        // then
        assertEquals("테스트용 타이틀1", postEntity.getTitle());
        assertEquals("테스트용 내용1", postEntity.getContent());

    }

    @Test
    @DisplayName("ReplyEntity 테스트")
    public void testReply() {
        // Given
        ReplyEntity replyEntity = new ReplyEntity();
        ReplyRequestDto replyRequestDto = new ReplyRequestDto();
        replyRequestDto.setContent("댓글내용입력");

        // When
        replyEntity = ReplyEntity.builder()
                .content(replyRequestDto.getContent())
                .build()
        ;

        // Then
        assertEquals("댓글내용입력", replyEntity.getContent());
    }


    @Test
    @DisplayName("UserEntity 테스트")
    public void testUser(){
        // Given
        User user = new User();
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setPassword("park");
        signUpRequestDto.setPassword("123456789");

        // When
        user = User.builder()
                .username("park")
                .password("123456789")
                .build();


        // Then
        assertEquals("park", user.getUsername());
        assertEquals("123456789",user.getPassword());

    }







}