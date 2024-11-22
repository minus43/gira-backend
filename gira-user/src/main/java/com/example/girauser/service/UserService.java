package com.example.girauser.service;

import com.example.girauser.dto.UserDto;
import com.example.girauser.entity.User;
import com.example.girauser.repository.UserRepository;
import com.example.girauser.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Qualifier("user-template")
    private final RedisTemplate<String, Object> redisTemplate;


    public void signIn(UserDto dto, HttpServletResponse response) throws Exception {

        // 로그인 오류 발생 가능
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow();

        String token = jwtTokenProvider.createToken(user.getEmail(),user.getRole());
        log.info("token: {}", token);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getRole());
        log.info("refreshToken: {}", refreshToken);
        redisTemplate.opsForValue().set(user.getEmail(), refreshToken, 240, TimeUnit.HOURS);

        // 헤더에 실어보낼 정보(팀정보 추가해서 보내야 함)
        response.addHeader("Authorization", "Bearer " + token);
        response.addHeader("Email", user.getEmail());
        response.addHeader("Role", user.getRole());
    }

    public void signUp(UserDto dto) throws Exception {
        User user = dto.toEntity(encoder);
        log.info("Save user: {}", user);
        userRepository.save(user);
    }

    public void modify(UserDto dto) {
    }

    public void delete(UserDto dto) {
    }

    public void myInfo() {
    }

    public void userList() {
    }

}
