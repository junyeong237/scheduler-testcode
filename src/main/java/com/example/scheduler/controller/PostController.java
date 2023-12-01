package com.example.scheduler.controller;


import com.example.scheduler.dto.PostRequestDto;
import com.example.scheduler.dto.PostResponseDto;
import com.example.scheduler.dto.ResponseEntityDto;
import com.example.scheduler.security.UserDetailsImpl;
import com.example.scheduler.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor

public class PostController {

    private final PostService postService;

    @PostMapping("/post/create")
    public ResponseEntityDto<PostResponseDto> create(
            @Valid
            @RequestBody PostRequestDto postRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails

    ){
         PostResponseDto postResponseDto = postService.create(postRequestDto, userDetails);
         return new ResponseEntityDto<PostResponseDto>
                 (HttpStatus.CREATED,"게시글 작성완료",postResponseDto);

    }

    @GetMapping("/post/id/{id}")
    public ResponseEntityDto<PostResponseDto> view(@PathVariable Long id){
        PostResponseDto postResponseDto = postService.getPost(id);
        return new ResponseEntityDto<PostResponseDto>
                (HttpStatus.OK, "게시글 조회 완료", postResponseDto);
    }
    @GetMapping("/post")
    public ResponseEntityDto<List<PostResponseDto>> listView(@AuthenticationPrincipal UserDetailsImpl userDetails){

        List<PostResponseDto> postResponseDtoList = postService.getPosts(userDetails);
        return new ResponseEntityDto<List<PostResponseDto>>
                (HttpStatus.OK, "해당 유저의 게시글 전체 조회 완료", postResponseDtoList);
    }

    @GetMapping("/postall")
    public ResponseEntityDto<List<PostResponseDto>> listViewAll(){

        List<PostResponseDto> postResponseDtoLists = postService.getPostsAll();
        return new ResponseEntityDto<List<PostResponseDto>>
                (HttpStatus.OK, "모든 유저의 게시글 전체 조회 완료", postResponseDtoLists);
    }


    @PutMapping("/post/update/{id}")
    public ResponseEntityDto<PostResponseDto> update(
            @PathVariable Long id,
            @RequestBody PostRequestDto postRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        PostResponseDto postResponseDto = postService.updatePost(id,postRequestDto,userDetails);

        return new ResponseEntityDto<PostResponseDto>(HttpStatus.OK, "게시글 수정완료",postResponseDto);

    }

    @GetMapping("/post/updatefinished/{id}")
    public ResponseEntityDto<PostResponseDto> updateFinished(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        PostResponseDto postResponseDto = postService.updateFinishedPost(id,userDetails);

        return new ResponseEntityDto<PostResponseDto>(HttpStatus.OK, "게시글 수정완료",postResponseDto);

    }

    @DeleteMapping("/post/delete/{id}")
    public ResponseEntityDto<?>  delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    )
    {

        postService.deletePost(id,userDetails);

        return new ResponseEntityDto<>(HttpStatus.OK , "게시글 삭제완료",null);
    }



}
