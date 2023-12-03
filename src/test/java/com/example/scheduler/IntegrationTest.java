package com.example.scheduler;

import com.example.scheduler.dto.*;
import com.example.scheduler.entity.ReplyEntity;
import com.example.scheduler.entity.User;
import com.example.scheduler.repository.ReplyRepository;
import com.example.scheduler.repository.UserRepository;
import com.example.scheduler.security.UserDetailsImpl;
import com.example.scheduler.service.PostService;
import com.example.scheduler.service.ReplyService;
import com.example.scheduler.service.UserService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 서버의 PORT 를 랜덤으로 설정합니다.
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 테스트 인스턴스의 생성 단위를 클래스로 변경합니다.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationTest {

    @Autowired
    PostService postService;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    ReplyService replyService;

    @Autowired
    ReplyRepository replyRepository;
    User user;

    PostResponseDto createdPost = null;
//    @Test
//    @Order(1)
//    @DisplayName("신규 회원가입")
//    void testSignup() {
//        // given
//        String username = "testUser1";
//        String password = "1234567890";
//
//        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(username, password);
//
//        // when
//        userService.signup(signUpRequestDto);
//
//        // then
//        User savedUser = userRepository.findByUsername(username)
//                .orElseThrow(() -> new AssertionError("회원가입이 정상적으로 이루어지지 않았습니다."));
//
//        assertNotNull(savedUser);
//        assertEquals(username, savedUser.getUsername());
//
//    }

    @Test
    @Order(2)
    @DisplayName("로그인했다 가정하고 게시물 생성 ")
    void testPostCreate() {
        //Given
        user = userRepository.findByUsername("testUser1").orElse(null);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        PostRequestDto postRequestDto = new PostRequestDto("게시물 제목", "게시물 내용");

        //When
        PostResponseDto postResponseDto = postService.create(postRequestDto, userDetails);

        // Then
        assertNotNull(postResponseDto);

        assertEquals("게시물 제목", postResponseDto.getTitle());
        assertEquals("게시물 내용", postResponseDto.getContent());
        this.createdPost = postResponseDto;

    }

    @Test
    @Order(3)
    @DisplayName("로그인했다 가정하고 게시물 수정")
    void testPostUpdate() {
        //Given
        user = userRepository.findByUsername("testUser1").orElse(null);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        PostRequestDto postRequestDto = new PostRequestDto("게시물 제목수정", "게시물 내용수정");
        Long postId = this.createdPost.getId();
        //여기서 createdPost를 쓰기 위해서는 통합 테스트로 전부 한꺼번에 실행해야한다.
        // 그러지않고 단위테스트로 실행하면 nullpointException이 일어난다.


        //When
        PostResponseDto postResponseDto = postService.updatePost(postId,postRequestDto, userDetails);

        // Then
        assertNotNull(postResponseDto);

        assertEquals("게시물 제목수정", postResponseDto.getTitle());
        assertEquals("게시물 내용수정", postResponseDto.getContent());

    }

    @Test
    @Order(3)
    @DisplayName("로그인 유저의 게시물조회")
    void testPostGet() {
        //Given
        user = userRepository.findByUsername("testUser1").orElse(null);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        //When
        List<PostResponseDto> postList = postService.getPosts(userDetails);

        // Then
        assertFalse(postList.isEmpty());

        assertEquals("게시물 제목수정", postList.get(0).getTitle());

    }


    @Test
    @Order(4)
    @DisplayName("모든 게시글 조회")
    void testPostGetAll() {
        //Given

        //When
        List<PostResponseDto> postList = postService.getPostsAll();

        // Then
        assertFalse(postList.isEmpty());

        for (PostResponseDto post : postList) {
            assertNotNull(post);
        }


    }

    @Test
    @Order(5)
    @DisplayName("로그인 유저의 댓글생성 ")
    void testReplyPost() {
        //Given
        user = userRepository.findByUsername("testUser1").orElse(null);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        ReplyRequestDto replyRequestDto = new ReplyRequestDto("댓글입니다.");
        Long postId = this.createdPost.getId();

        //When
        ReplyResponseDto responseDto = replyService.create(replyRequestDto, postId,userDetails);

        // Then
        assertNotNull(responseDto);

        assertEquals("댓글입니다.", responseDto.getContent());

    }


    @Test
    @Order(6)
    @DisplayName("로그인 유저의 댓글수정 ")
    void testReplyUpdate() {
        //Given
        user = userRepository.findByUsername("testUser1").orElse(null);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        ReplyRequestDto replyRequestDto = new ReplyRequestDto("댓글 수정입니다.");
        Long postId = this.createdPost.getId();

        //When
        ReplyResponseDto responseDto = replyService.updateReply(postId,replyRequestDto,userDetails);

        // Then
        assertNotNull(responseDto);

        assertEquals("댓글 수정입니다.", responseDto.getContent());

    }

    @Test
    @Order(7)
    @DisplayName("로그인 유저의 댓글삭제")
    void testReplyDelete() {
        //Given
        user = userRepository.findByUsername("testUser1").orElse(null);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        Long postId = this.createdPost.getId();

        //When
        replyService.deleteReply(postId,userDetails);

        // Then
        //Mockito.verify(replyRepository).delete(any(ReplyEntity.class));
        //Mock객체가 아닌 @AutoWired객체의 경우 verify를 사용불가능


        //따라서 억지검증을 하자면

        ReplyEntity reply = replyRepository.findByUserAndId(user,postId).orElse(null);

        assertNull(reply);

    }







}