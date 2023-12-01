package com.example.scheduler.repository;

import com.example.scheduler.entity.PostEntity;
import com.example.scheduler.entity.ReplyEntity;
import com.example.scheduler.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {

    Optional<ReplyEntity> findByUserAndId(User user, Long id);
}
