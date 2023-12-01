package com.example.scheduler.dto;

import com.example.scheduler.entity.PostEntity;
//import com.example.scheduler.entity.ReplyEntity;
import com.example.scheduler.entity.ReplyEntity;
import com.example.scheduler.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponseDto {

    private Long id;
    private Long userId;
    private User user;

    private Boolean finished;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime modifedAt;

    private List<ReplyResponseDto> replyList = new ArrayList<>();
    //private List<ReplyEntity> replyList = new ArrayList<>();

    public PostResponseDto(PostEntity postEntity){
        this.id = postEntity.getId();
        this.userId = postEntity.getUser().getId();
        this.user = postEntity.getUser();
        this.finished = postEntity.getFinished();
        this.title = postEntity.getTitle();
        this.content = postEntity.getContent();
        this.createdAt = postEntity.getCreatedAt();
        this.modifedAt = postEntity.getModifiedAt();
        //this.replyList = postEntity.getReplyList();
        this.replyList = Optional.ofNullable(postEntity.getReplyList())
                .map(replys -> replys.stream().map(ReplyResponseDto::new).toList())
                .orElse(Collections.emptyList());
    }
}
