package com.example.scheduler.entity;

import com.example.scheduler.dto.TimeStamped;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User{ //extends TimeStamped
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String username;

        @Column(nullable = false)
        private String password;


        @OneToMany(mappedBy = "user") //,fetch = FetchType.EAGER
        private List<PostEntity> postList = new ArrayList<>();

        @OneToMany(mappedBy = "user") //,fetch = FetchType.EAGER
        private List<ReplyEntity> replylist = new ArrayList<>();

        public void addPostList(PostEntity post){
                this.postList.add(post);
                post.setUser(this);
        }
        // 객채스럽게 사용하기 위함

        public void addReplyList(ReplyEntity reply){
                this.replylist.add(reply);
                reply.setUser(this);
        }



}


