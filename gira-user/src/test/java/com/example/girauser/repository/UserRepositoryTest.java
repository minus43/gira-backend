package com.example.girauser.repository;

import com.example.girauser.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;

    @Test
    @DisplayName("dummyUser 추가")
    void dummyUser() {
        for(int i =0; i<100; i++){
            userRepository.save(User.builder()
                    .email("test"+i+"@test.com")
                    .nickName("test"+i)
                    .password(encoder.encode("test"+i))
                    .build());
        }
    }
}