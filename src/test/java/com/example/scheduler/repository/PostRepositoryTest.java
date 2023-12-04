package com.example.scheduler.repository;

import com.example.scheduler.entity.PostEntity;
import com.example.scheduler.entity.User;
import com.example.scheduler.repository.PostRepository;
import com.example.scheduler.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    private User user;
    private User tempUser;

    private PostEntity post;
    @BeforeEach
    void setup(){
        // Given
        user = User.builder()
                .username("park")
                .password("123456789")
                .build();
        //userRepository.save(user);

        tempUser = User.builder()
                .username("아무사람")
                .password("123456789")
                .build();
        userRepository.save(tempUser);

        post = PostEntity.builder()
                .content("내용입니다.")
                .title("제목입니다.")
                .user(user)
                .build();
        //postRepository.save(post);
        postRepository.save(post);

        userRepository.save(user);

    }
    @Test
    void test() {

        // When
        Optional<PostEntity> foundPost = postRepository.findByUserAndId(user, post.getId());

        // Then
        assertTrue(foundPost.isPresent());
        assertEquals(post.getId(), foundPost.get().getId());
    }

    @Test
    void testFail() {

        // When
        Optional<PostEntity> foundPost = postRepository.findByUserAndId(tempUser, post.getId());

        // Then
        assertFalse(foundPost.isPresent());
    }

    @Test
    @DisplayName("해당하는 유저의 모든 게시글 가져오기 성공")
    void testPostAllByUsr() {
        // Given


        PostEntity post1 = PostEntity.builder()
                .content("내용입니다.")
                .title("제목입니다.")
                .user(user)
                .build();
        postRepository.save(post1);


        PostEntity post2 = PostEntity.builder()
                .content("내용2입니다.")
                .title("제목2입니다.")
                .user(user)
                .build();
        postRepository.save(post2);


        // When
        List<PostEntity> postList = postRepository.findAllByUser(user);

        // Then
        assertEquals(3, postList.size());
        assertTrue(postList.contains(post1));
        assertTrue(postList.contains(post2));
    }

    @Test
    @DisplayName("해당하는 유저의 모든 게시글 가져오기 실패")
    void testPostAllByUsrFail() {
        // Given

        PostEntity post1 = PostEntity.builder()
                .content("내용입니다.")
                .title("제목입니다.")
                .user(user)
                .build();
        postRepository.save(post1);


        PostEntity post2 = PostEntity.builder()
                .content("내용2입니다.")
                .title("제목2입니다.")
                .user(user)
                .build();
        postRepository.save(post2);


        // When
        List<PostEntity> postList = postRepository.findAllByUser(tempUser);

        // Then
        assertTrue(postList.isEmpty());
    }



}
