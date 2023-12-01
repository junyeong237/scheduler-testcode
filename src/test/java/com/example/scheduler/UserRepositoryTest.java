package com.example.scheduler;

import com.example.scheduler.entity.User;
import com.example.scheduler.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("UserRepository 테스트")
    void test() {
        // Given
        String username = "park";
        User user = User.builder().username(username).password("password").build();
        userRepository.save(user);

        // When
        Optional<User> resultUser = userRepository.findByUsername(username);

        // Then
        assertTrue(resultUser.isPresent());
        assertEquals(username, resultUser.get().getUsername());
    }


    @Test
    @DisplayName("UserRepository 테스트 실패")
    void testFail() {
        // Given
        String username = "park";
        User user = User.builder().username(username).password("password").build();
        userRepository.save(user);

        // When
        Optional<User> resultUser = userRepository.findByUsername("다른거");

        // Then
        assertFalse(resultUser.isPresent());
    }


}
