package com.example.scheduler;

import com.example.scheduler.entity.PostEntity;
import com.example.scheduler.entity.User;
import com.example.scheduler.repository.PostRepository;
import com.example.scheduler.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@ActiveProfiles("test")
public class PostRepositoryTest {


    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void test() {
        // Given
        User user = User.builder()
                .username("park")
                .password("123456789")
                .build();
        userRepository.save(user);

        PostEntity post = PostEntity.builder()
                        .content("내용입니다.")
                        .title("제목입니다.")
                        .user(user)
                         .build();
        postRepository.save(post);

        // When
        Optional<PostEntity> foundPost = postRepository.findByUserAndId(user, post.getId());

        // Then
        assertTrue(foundPost.isPresent());
        assertEquals(post.getId(), foundPost.get().getId());
    }

    @Test
    void testFail() {
        // Given
        User user = User.builder()
                .username("park")
                .password("123456789")
                .build();
        userRepository.save(user);

        User tempUser = User.builder()
                .username("아무사람")
                .password("123456789")
                .build();
        userRepository.save(tempUser);

        PostEntity post = new PostEntity();
        post.setUser(user);
        postRepository.save(post);

        // When
        Optional<PostEntity> foundPost = postRepository.findByUserAndId(tempUser, post.getId());

        // Then
        assertFalse(foundPost.isPresent());
    }


}
