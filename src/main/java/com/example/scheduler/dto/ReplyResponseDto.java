package com.example.scheduler.dto;

import com.example.scheduler.entity.PostEntity;
import com.example.scheduler.entity.ReplyEntity;
import com.example.scheduler.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyResponseDto {

    private Long id;
    //private Long userId;
    private User user;

    private Long postId;
    private PostEntity post;

    private String content;
    private LocalDateTime createdAt;

    private LocalDateTime modifedAt;
    public ReplyResponseDto(ReplyEntity replyEntity) {

        this.id = replyEntity.getId();
        //this.userId = replyEntity.getUser().getId();
        this.user = replyEntity.getUser();
        this.postId = replyEntity.getPost().getId();
        this.post = replyEntity.getPost();
        this.content = replyEntity.getContent();
        this.createdAt = replyEntity.getCreatedAt();
        this.modifedAt = replyEntity.getModifiedAt();

    }
}
