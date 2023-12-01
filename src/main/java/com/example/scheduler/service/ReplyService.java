package com.example.scheduler.service;

import com.example.scheduler.dto.PostRequestDto;
import com.example.scheduler.dto.PostResponseDto;
import com.example.scheduler.dto.ReplyRequestDto;
import com.example.scheduler.dto.ReplyResponseDto;
import com.example.scheduler.entity.PostEntity;
import com.example.scheduler.entity.ReplyEntity;
import com.example.scheduler.entity.User;
import com.example.scheduler.repository.PostRepository;
import com.example.scheduler.repository.ReplyRepository;
import com.example.scheduler.repository.UserRepository;
import com.example.scheduler.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;

    public ReplyResponseDto create(ReplyRequestDto replyRequestDto, Long postId, UserDetailsImpl userDetails){

        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        User user = getUser(userDetails);

        ReplyEntity replyEntity = ReplyEntity.builder()
                .post(postEntity)
                .user(user)
                .username(user.getUsername())
                .content(replyRequestDto.getContent())
                .build()
                ;

        //user.addReplyList(replyEntity);
        replyRepository.save(replyEntity);
        //postRepository.save(postEntity);
        //userRepository.save(user);

        return new ReplyResponseDto(replyEntity);

    }

    @Transactional
    public ReplyResponseDto updateReply(Long id, ReplyRequestDto replyRequestDto, UserDetailsImpl userDetails) {

        User user = getUser(userDetails);
        ReplyEntity replyEntity = getReplyByUser(user,id);

        replyEntity.update(replyRequestDto);

        return new ReplyResponseDto(replyEntity);


    }

    public void deleteReply(Long id, UserDetailsImpl userDetails) {

        User user = getUser(userDetails);

        ReplyEntity replyEntity = getReplyByUser(user,id);

        replyRepository.delete(replyEntity);

    }

    public ReplyEntity getReply(Long id, UserDetailsImpl userDetails) {

        User user = getUser(userDetails);

        ReplyEntity replyEntity = getReplyByUser(user,id);

        return replyEntity;
    }

    private User getUser(UserDetailsImpl userDetails){
        return userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다.."));

    }

    private ReplyEntity getReplyByUser(User user, Long id){

        return replyRepository.findByUserAndId(user,id)
                .orElseThrow(() -> new IllegalArgumentException("알맞는 유저가 아니거나 댓글을 찾을 수 없습니다."));
    }

}
