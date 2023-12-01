package com.example.scheduler.dto;

import com.example.scheduler.entity.PostEntity;
import com.example.scheduler.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String username;
    private List<PostEntity> postList = new ArrayList<>();


    public UserResponseDto(User user){
        this.username = user.getUsername();
        this.postList = user.getPostList();
    }
}
