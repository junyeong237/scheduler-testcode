package com.example.scheduler.entity;

import com.example.scheduler.dto.PostRequestDto;
import com.example.scheduler.dto.ReplyRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Entity
@Table(name = "reply")
@NoArgsConstructor
@AllArgsConstructor
public class ReplyEntity extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore
    //@ToString.Exclude
    private User user;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String username;

    @ManyToOne //(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    @JsonIgnore
    private PostEntity post;

    public void update(ReplyRequestDto replyRequestDto) {

        this.content = replyRequestDto.getContent();


    }

}