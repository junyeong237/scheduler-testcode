package com.example.scheduler.controller;


import com.example.scheduler.config.WebSecurityConfig;
import com.example.scheduler.dto.PostRequestDto;
import com.example.scheduler.dto.PostResponseDto;
import com.example.scheduler.dto.ReplyRequestDto;
import com.example.scheduler.dto.ReplyResponseDto;
import com.example.scheduler.entity.PostEntity;
import com.example.scheduler.entity.ReplyEntity;
import com.example.scheduler.entity.User;
import com.example.scheduler.filter.MockSpringSecurityFilter;
import com.example.scheduler.repository.PostRepository;
import com.example.scheduler.security.UserDetailsImpl;
import com.example.scheduler.service.PostService;
import com.example.scheduler.service.ReplyService;
import com.example.scheduler.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        controllers = {ReplyController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
public class ReplyControllerTest {

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

    @MockBean
    PostRepository postRepository;


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
    @DisplayName("댓글 등록")
    void testPostCreate() throws Exception {
        // given


        Long postId = 1L;
        PostEntity postEntity = new PostEntity();
        postEntity.setId(postId);


        String replyContent = "댓글입니다.";
        ReplyRequestDto replyRequestDto = new ReplyRequestDto(replyContent);

        String ReplyInfo = objectMapper.writeValueAsString(replyRequestDto);

        ReplyEntity reply = ReplyEntity.builder()
                        .content(replyContent)
                        .post(postEntity)
                        .build();


        given(replyService.create(replyRequestDto, postId, testUserDetails))
                .willReturn(new ReplyResponseDto(reply));


        // when - then
        mvc.perform(post("/reply/create/{id}",postId)
                        .content(ReplyInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        //.accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("CREATED"))
                .andExpect(jsonPath("$.resultMessage").value("댓글 작성 완료"))
                .andExpect(jsonPath("$.data.content").value(replyContent))
                .andDo(print());
    }


    @Test
    @DisplayName("댓글 수정")
    void testPostUpdate() throws Exception {
        // given
        Long postId = 1L;
        PostEntity postEntity = new PostEntity();
        postEntity.setId(postId);

        Long replyId = 1L;
        String replyContent = "댓글수정입니다.";
        ReplyRequestDto replyRequestDto = new ReplyRequestDto(replyContent);

        String ReplyInfo = objectMapper.writeValueAsString(replyRequestDto);

        ReplyEntity reply = ReplyEntity.builder()
                .id(replyId)
                .content(replyContent)
                .post(postEntity)
                .build();


        given(replyService.updateReply(replyId, replyRequestDto, testUserDetails))
                .willReturn(new ReplyResponseDto(reply));


        // when - then
        mvc.perform(put("/reply/update/{id}",replyId)
                        .content(ReplyInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        //.accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("OK"))
                .andExpect(jsonPath("$.resultMessage").value("댓글 수정 완료"))
                .andExpect(jsonPath("$.data.content").value(replyContent))
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 삭제")
    void testPostDelete() throws Exception {
        // given

        Long replyId = 1L;



        doNothing().when(replyService).deleteReply(replyId,testUserDetails);


        // when - then
        mvc.perform(delete("/reply/delete/{id}",replyId)

                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("OK"))
                .andExpect(jsonPath("$.resultMessage").value("댓글 삭제 완료"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

}
