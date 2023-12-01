package com.example.scheduler.controller;

import com.example.scheduler.dto.*;
import com.example.scheduler.entity.ReplyEntity;
import com.example.scheduler.security.UserDetailsImpl;
import com.example.scheduler.service.ReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @GetMapping("/reply/{replyId}")
    public ResponseEntityDto<ReplyEntity> getReply(
            @PathVariable Long replyId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ReplyEntity replyEntity =  replyService.getReply(replyId,userDetails);
        return new ResponseEntityDto<ReplyEntity>(HttpStatus.OK, "댓글 조회 완료",replyEntity);
    }

    @PostMapping("/reply/create/{postId}")
    public ResponseEntityDto<ReplyResponseDto> create(
            @Valid
            @RequestBody ReplyRequestDto replyRequestDto,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ReplyResponseDto responseDto = replyService.create(replyRequestDto, postId,userDetails);

        return new ResponseEntityDto<ReplyResponseDto>(HttpStatus.CREATED, "댓글 작성 완료",responseDto);
    }

    @PutMapping("/reply/update/{id}")
    public ResponseEntityDto<ReplyResponseDto> update(
            @PathVariable Long id,
            @RequestBody ReplyRequestDto replyRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
       ReplyResponseDto responseDto = replyService.updateReply(id,replyRequestDto,userDetails);
       return new ResponseEntityDto<ReplyResponseDto>(HttpStatus.OK, "댓글 수정 완료",responseDto);

    }

    @DeleteMapping("/reply/delete/{id}")
    public ResponseEntityDto<?> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    )
    {
        replyService.deleteReply(id,userDetails);
        return new ResponseEntityDto<>(HttpStatus.OK, "댓글 삭제 완료",null);
    }



}
