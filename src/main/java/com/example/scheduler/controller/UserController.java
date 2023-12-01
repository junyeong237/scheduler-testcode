package com.example.scheduler.controller;


import com.example.scheduler.dto.*;

import com.example.scheduler.entity.User;
import com.example.scheduler.entity.UserRoleEnum;
import com.example.scheduler.security.UserDetailsImpl;
import com.example.scheduler.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor


public class UserController {

    private final UserService userService;

    @GetMapping("/user/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/user/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/user/signup")
    @ResponseBody
    public ResponseEntityDto<?> signup(@Valid @ModelAttribute SignUpRequestDto requestDto) {
        //, BindingResult bindingResult //이걸 뺴주니까 MethodArgumentNotValidException 정상작동함
       //Validation 예외처리
//        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
//        if(fieldErrors.size() > 0) {
//            for (FieldError fieldError : bindingResult.getFieldErrors()) {
//                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
//            }
//            return new ResponseEntityDto<>(HttpStatus.BAD_REQUEST,"회원가입이 안됩니다.",null);
//        }
        userService.signup(requestDto);
        return new ResponseEntityDto<>(HttpStatus.CREATED,"회원가입이 완료되었습니다.",null);

    }

    @GetMapping("/user-info")
    @ResponseBody
    public UserInfoDto getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String username = userDetails.getUser().getUsername();
        return new UserInfoDto(username);
    }

    @GetMapping("/alluser")
    @ResponseBody
    public List<UserResponseDto> getAllUser() {

        List<UserResponseDto> userList = userService.getAllUser();
        return userList;
    }

}
