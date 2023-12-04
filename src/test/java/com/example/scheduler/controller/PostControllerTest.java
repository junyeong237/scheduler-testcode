package com.example.scheduler.controller;


import com.example.scheduler.config.WebSecurityConfig;
import com.example.scheduler.controller.PostController;
import com.example.scheduler.dto.PostRequestDto;
import com.example.scheduler.dto.PostResponseDto;
import com.example.scheduler.dto.ResponseEntityDto;
import com.example.scheduler.dto.UserResponseDto;
import com.example.scheduler.entity.PostEntity;
import com.example.scheduler.entity.User;
import com.example.scheduler.entity.UserRoleEnum;
import com.example.scheduler.filter.MockSpringSecurityFilter;
import com.example.scheduler.security.UserDetailsImpl;
import com.example.scheduler.service.PostService;
import com.example.scheduler.service.ReplyService;
import com.example.scheduler.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {PostController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
public class PostControllerTest {

    private MockMvc mvc;

    private Principal mockPrincipal;
    private User testUser;

    private UserDetailsImpl testUserDetails;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @MockBean
    PostService postService;

    @MockBean
    ReplyService replyService;

    private void mockUserSetup() {
        // Mock 테스트 유져 생성
        String username = "park";
        String password = "123456789";
        testUser = User.builder()
                .username(username)
                .password(password)
                .build();

        testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
    }
    @BeforeEach
    public void setup() {
        this.mockUserSetup();
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

    @Test
    @DisplayName("신규 게시글 등록")
    void testPostCreate() throws Exception {
        // given
        String title = "제목입니다.";
        String content = "내용입니다.";
        PostRequestDto postRequestDto = new PostRequestDto(title,content);

        String postInfo = objectMapper.writeValueAsString(postRequestDto);

        // when - then
        mvc.perform(post("/post/create")
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON) //이건 없으면 오류
                         //content가 json타입으로 되어있다고 알려줌
                        .accept(MediaType.APPLICATION_JSON) //굳이 있어야하나? //없어도된다.
                        // 이 api가 json타입의 데이터를 허용해주겠다.............
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 수정")
    void testPostUpdate() throws Exception {
        // given
        Long postId = 1L;
        String title = "수정 제목입니다.";
        String content = "수정 내용입니다.";
        PostRequestDto postRequestDto = new PostRequestDto(title,content);

        PostEntity newPost = PostEntity.builder()
                                    .Id(postId)
                .user(testUserDetails.getUser())
                                    .title(title)
                                    .content(content)
                                    .build();


//
        given(postService.updatePost(postId, postRequestDto, testUserDetails))
                .willReturn(new PostResponseDto(newPost));


        String postUpdateInfo = objectMapper.writeValueAsString(postRequestDto);
        // when - then
        mvc.perform(put("/post/update/{id}", postId)
                        .content(postUpdateInfo)
                        .contentType(MediaType.APPLICATION_JSON) //필수
                        .accept(MediaType.APPLICATION_JSON) //없어도 오류 안남
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.resultCode").value(HttpStatus.OK))
                .andExpect(jsonPath("$.resultCode").value("OK"))
                .andExpect(jsonPath("$.resultMessage").value("게시글 수정완료"))
                .andExpect(jsonPath("$.data.title").value(title))
                .andExpect(jsonPath("$.data.content").value(content));
    }

    @Test
    @DisplayName("게시글 수정-실패")
    void testPostUpdateFail() throws Exception {
        // given
        Long postId = 1L;
        String title = "수정 제목입니다.";
        String content = "수정 내용입니다.";
        PostRequestDto postRequestDto = new PostRequestDto(title,content);

        PostEntity newPost = PostEntity.builder()
                .Id(postId)
                .user(testUserDetails.getUser())
                .title(title)
                .content(content)
                .build();


//
        given(postService.updatePost(postId, postRequestDto, testUserDetails))
                .willThrow(new IllegalArgumentException());


        String postUpdateInfo = objectMapper.writeValueAsString(postRequestDto);
        // when - then
        mvc.perform(put("/post/update/{id}", postId)
                        .content(postUpdateInfo)
                        .contentType(MediaType.APPLICATION_JSON) //필수
                        .accept(MediaType.APPLICATION_JSON) //없어도 오류 안남
                        .principal(mockPrincipal)
                )
                .andExpect(status().isBadRequest());


    }

    @Test
    @DisplayName("단일 게시글 조회")
    void testPostGet() throws Exception {
        // given

        // when, then
        mvc.perform(get("/post/id/{id}",1L)
                        .principal(mockPrincipal))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("해당 유저의 게시글 전체 조회")
    void testPostUserGetAll() throws Exception {
        // given

        // given
        Long postId1 = 1L;
        String title = "제목1입니다.";
        String content = "내용1입니다.";
        PostEntity newPost1 = PostEntity.builder()
                .Id(postId1)
                .user(testUserDetails.getUser())
                .title(title)
                .content(content)
                .build();

        Long postId2 = 2L;
        String title2 = "제목2입니다.";
        String content2 = "내용2입니다.";
        PostEntity newPost2 = PostEntity.builder()
                .Id(postId2)
                .user(testUserDetails.getUser())
                .title(title2)
                .content(content2)
                .build();

        List<PostResponseDto> postList = Arrays.asList(
                new PostResponseDto(newPost1),
                new PostResponseDto(newPost2)
        );

        given(postService.getPosts(testUserDetails))
                .willReturn(postList);

        // when, then
        mvc.perform(get("/post")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("OK"))
                .andExpect(jsonPath("$.resultMessage").value("해당 유저의 게시글 전체 조회 완료"))
                .andExpect(jsonPath("$.data[0].title").value(title))
                .andExpect(jsonPath("$.data[0].content").value(content));

    }


    @Test
    @DisplayName("해당 유저의 게시글 삭제")
    void testPostDelete() throws Exception {
        // given
        Long postId1 = 1L;
        String title = "제목1입니다.";
        String content = "내용1입니다.";
        PostEntity newPost1 = PostEntity.builder()
                .Id(postId1)
                .user(testUserDetails.getUser())
                .title(title)
                .content(content)
                .build();

        //given(postService.deletePost(postId1, testUserDetails))
        // deletePost가 void 형을 반환해서 given. willReturn 사용불가

        doNothing().when(postService).deletePost(postId1, testUserDetails);
        //doNothing()은 Mockito에서 사용되는 메서드 중 하나로, 특정 메서드의 호출 시 아무런 동작도 하지 않도록 설정하는 데 사용됩니다.

        // when - then
        mvc.perform(delete("/post/delete/{id}", postId1)
                        .accept(MediaType.APPLICATION_JSON) //없어도 오류 안남

                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("OK"))
                .andExpect(jsonPath("$.resultMessage").value("게시글 삭제완료"))
                .andExpect(jsonPath("$.data").doesNotExist());
       // null 값이 JSON 응답으로 전송될 때, JSON에서는 해당 필드가 존재하지 않는 것과 null이 모두 동일한 의미를 가집니다

        // then
        verify(postService, times(1)).deletePost(any(Long.class),any(UserDetailsImpl.class));
    }



}
