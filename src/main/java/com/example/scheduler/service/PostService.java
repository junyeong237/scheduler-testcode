package com.example.scheduler.service;

import com.example.scheduler.dto.PostRequestDto;
import com.example.scheduler.dto.PostResponseDto;
import com.example.scheduler.entity.PostEntity;
import com.example.scheduler.entity.User;
import com.example.scheduler.repository.PostRepository;
import com.example.scheduler.repository.UserRepository;
import com.example.scheduler.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostResponseDto create(PostRequestDto postRequestDto, UserDetailsImpl userDetails) {

        User user = getUser(userDetails);

        PostEntity postEntity = PostEntity.builder()
                .user(user)
                .finished(false)
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .build();

        postRepository.save(postEntity);
        return new PostResponseDto(postEntity);
    }

    public PostResponseDto getPost(Long id) {

        PostEntity post = getPostEntity(id);

        return new PostResponseDto(post);
    }

    @Transactional(readOnly = true) // 오류가떠서 일단 이렇게 바꿈... 지연로딩시 transactional 필요
    public List<PostResponseDto> getPosts(UserDetailsImpl userDetails) {

        User user = getUser(userDetails);

        var postList = postRepository.findAllByUser(user);
        if(postList.isEmpty()){
            return Collections.emptyList();
        }
        return postList
                .stream()
                .map(PostResponseDto::new)
                .toList();
    }


    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto postRequestDto, UserDetailsImpl userDetails) {

        User user = getUser(userDetails);

        PostEntity post = getPostEntityByUser(id,user); //

        post.update(postRequestDto);

        return new PostResponseDto(post);
    }

    public void deletePost(Long postId,UserDetailsImpl userDetails) {
        User user = getUser(userDetails);

        PostEntity postEntity = getPostEntityByUser(postId, user);

        postRepository.delete(postEntity);
    }

    public List<PostResponseDto> getPostsAll() {

        List<PostEntity> postList = postRepository.findAll();
        if(postList.isEmpty()){
            return Collections.emptyList();
        }
        return postList
                .stream()
                .map(PostResponseDto::new)
                .toList();


    }
    @Transactional
    public PostResponseDto updateFinishedPost(Long id, UserDetailsImpl userDetails) {

        User user = getUser(userDetails);

        PostEntity post = getPostEntityByUser(id,user);

        post.setFinished((!post.getFinished()));
        return new PostResponseDto(post);
    }

    private User getUser(UserDetailsImpl userDetails){
        return userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
    }

    private PostEntity getPostEntity(Long id){
        PostEntity post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));
        return post;
    }
    private PostEntity getPostEntityByUser(Long id, User user){
        PostEntity post = postRepository.findByUserAndId(user,id).orElseThrow(
                () -> new IllegalArgumentException("알맞는 사용자가 아니거나 해당 게시글을 찾을 수 없습니다."));
        return post;
    }

}

