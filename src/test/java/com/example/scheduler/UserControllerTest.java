package com.example.scheduler;

import com.example.scheduler.config.WebSecurityConfig;
import com.example.scheduler.controller.UserController;
import com.example.scheduler.dto.UserResponseDto;
import com.example.scheduler.entity.User;
import com.example.scheduler.filter.MockSpringSecurityFilter;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = {UserController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
class UserControllerTest {
    private MockMvc mvc;

    private Principal mockPrincipal;

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

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

    @Test
    @DisplayName("로그인 Page")
    void testGetLogin() throws Exception {
        // when - then
        mvc.perform(get("/user/login-page"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andDo(print());
    }


    @Test
    @DisplayName("회원 가입 요청 처리")
    void testSignup() throws Exception {
        // given
        MultiValueMap<String, String> signupRequestForm = new LinkedMultiValueMap<>();
        signupRequestForm.add("username", "leee");
        signupRequestForm.add("password", "123456789");

        // when - then
        mvc.perform(post("/user/signup")
                        //.contentType(MediaType.APPLICATION_FORM_URLENCODED)  // 폼 데이터를 전송할 것임을 명시
                        .params(signupRequestForm)
                        //post 메서드에도 .param()을 사용할 수 있지만,
                        //이는 주로 application/x-www-form-urlencoded 형식의 데이터를 전송할 때 유용합니다.


                )

                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("유저리스트 가져오기 ")
    void testGetAllUsers() throws Exception {
        // Given
        User testUser1 = User.builder()
                .username("park")
                .password("123456789")
                .build();

        User testUser2 = User.builder()
                .username("hwang")
                .password("123456789")
                .build();

        List<UserResponseDto> UserList = Arrays.asList(
                new UserResponseDto(testUser1),
                new UserResponseDto(testUser2)
        );
        given(userService.getAllUser()).willReturn(UserList);

        // When, Then
        mvc.perform(get("/alluser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("park"))
                //JSON 응답에서 첫 번째 요소의 username 필드 값을 "park"과 일치하는지 확인
                .andExpect(jsonPath("$[1].username").value("hwang"));

    }


}
