package com.example.scheduler.entity;



import com.example.scheduler.dto.PostRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@Table(name = "post")
@NoArgsConstructor
@AllArgsConstructor

public class PostEntity extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private Boolean finished;

    private String title;

    private String content;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "post",cascade = {CascadeType.REMOVE})
    private List<ReplyEntity> replyList = new ArrayList<>();

    public void update(PostRequestDto postRequestDto) {

        this.title = postRequestDto.getTitle();
        this.content = postRequestDto.getContent();

    }


}
