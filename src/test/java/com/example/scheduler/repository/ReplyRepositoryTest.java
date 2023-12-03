package com.example.scheduler.repository;

import com.example.scheduler.entity.PostEntity;
import com.example.scheduler.entity.ReplyEntity;
import com.example.scheduler.entity.User;
import com.example.scheduler.repository.PostRepository;
import com.example.scheduler.repository.ReplyRepository;
import com.example.scheduler.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ReplyRepositoryTest {

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("ReplyRepository 테스트")
    void test() {
        // Given
        User user = User.builder()
                        .username("park")
                        .password("123456789")
                        .build();

        userRepository.save(user);

        PostEntity post = new PostEntity();
        post.setUser(user);
        postRepository.save(post);

        ReplyEntity reply = ReplyEntity.builder()
                        .post(post)
                        .user(user)
                        .username(user.getUsername())
                        .content("댓글입니다.")
                        .build();

        replyRepository.save(reply);

        // When
        Optional<ReplyEntity> resultReply = replyRepository.findByUserAndId(user, post.getId());

        // Then
        assertTrue(resultReply.isPresent());
        assertEquals(user, resultReply.get().getUser());
        assertEquals("댓글입니다.", resultReply.get().getContent());
    }

    @Test
    @DisplayName("ReplyRepository 테스트 실패")
    void testFail() {
        // Given
        User user = User.builder()
                .username("park")
                .password("123456789")
                .build();

        userRepository.save(user);

        PostEntity post = new PostEntity();
        post.setUser(user);
        postRepository.save(post);

        User tempUser = User.builder()
                .username("아무사람")
                .password("123456789")
                .build();
        userRepository.save(tempUser); //이거 안적으면 fail뜨네? ????????????????

        ReplyEntity reply = ReplyEntity.builder()
                .post(post)
                .user(user)
                .username(user.getUsername())
                .content("댓글입니다.")
                .build();

        replyRepository.save(reply);


        // When
        Optional<ReplyEntity> resultReply = replyRepository.findByUserAndId(tempUser, post.getId());


        // Then
        assertFalse(resultReply.isPresent());
    }


}
