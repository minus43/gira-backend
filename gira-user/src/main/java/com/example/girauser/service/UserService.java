package com.example.girauser.service;

import com.example.girauser.dto.UserDto;
import com.example.girauser.dto.UserResDto;
import com.example.girauser.entity.User;
import com.example.girauser.repository.UserRepository;
import com.example.girauser.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        log.info("signIn");
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow();

        String accessToken = jwtTokenProvider.createToken(user.getEmail(),user.getRole());
        log.info("accessToken: {}", accessToken);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getRole());
        log.info("refreshToken: {}", refreshToken);
        redisTemplate.opsForValue().set(user.getEmail(), refreshToken, 240, TimeUnit.HOURS);

        // 헤더에 실어보낼 정보
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addHeader("email", user.getEmail());
        response.addHeader("nickName", user.getNickName());
        response.addHeader("role", user.getRole());
    }

    public void signUp(UserDto dto) throws Exception {
        log.info("signUp");
        User user = dto.toEntity(encoder);
        log.info("Save user: {}", user);
        userRepository.save(user);
    }

    public void modify(UserDto dto, HttpServletRequest request) throws Exception {
        log.info("modify");
        String email = request.getHeader("email");
        User user = userRepository.findByEmail(email).orElseThrow();
        if(!dto.getNickName().isEmpty()){
            user.setNickName(dto.getNickName());
        }
        if(!dto.getPassword().isEmpty()){
            user.setPassword(encoder.encode(dto.getPassword()));
        }
        userRepository.save(user);
        log.info("Modify user: {}", user);
    }

    public void delete(HttpServletRequest request) throws Exception {
        log.info("delete");
        String email = request.getHeader("email");
        User user = userRepository.findByEmail(email).orElseThrow();
        userRepository.delete(user);
        redisTemplate.delete(user.getEmail());
    }

    public List<UserResDto> userList() throws Exception {
        log.info("userList");
        List<UserResDto> userList = new ArrayList<>();
        userRepository.findAll().forEach(user -> {
            userList.add(
            UserResDto.builder()
                    .email(user.getEmail())
                    .nickName(user.getNickName())
                    .build()
            );
        });
        return userList;

    }

    public void refresh(Map<String, String> email, HttpServletResponse response) throws Exception {
        log.info("refresh");
        User user = userRepository.findByEmail(email.get("email")).orElseThrow();

        Object refreshToken = redisTemplate.opsForValue().get(email.get("email"));
        if(refreshToken == null){
            throw new Exception("refresh token is expired");
        }
        String newAccessToken = jwtTokenProvider.createToken(user.getEmail(),user.getRole());
        response.addHeader("Authorization", "Bearer " + newAccessToken);
    }
}
