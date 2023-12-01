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

   //@ManyToOne(fetch = FetchType.LAZY) //왜 안될까

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore
    //@ToString.Exclude
    private User user;

    //@Column(columnDefinition = "TEXT") //TEXT속성을 맞추는거
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String username;

    @ManyToOne //(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    @JsonIgnore
    //@ToString.Exclude
    private PostEntity post;

    public void update(ReplyRequestDto replyRequestDto) {

        this.content = replyRequestDto.getContent();


    }

}