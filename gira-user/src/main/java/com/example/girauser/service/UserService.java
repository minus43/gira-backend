package com.example.girauser.service;

import com.example.girauser.dto.UserDto;
import com.example.girauser.dto.UserResDto;
import com.example.girauser.entity.User;
import com.example.girauser.repository.UserRepository;
import com.example.girauser.util.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Qualifier("user-template")
    private final RedisTemplate<String, Object> redisTemplate;



    public HttpServletResponse signIn(UserDto dto, HttpServletResponse response) {
        log.info("signIn");
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(() ->
                new EntityNotFoundException("User with email " + dto.getEmail() + " not found")
        );
        if(!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Wrong password");
        }
        String accessToken = jwtTokenProvider.createToken(user.getEmail(), user.getRole());
        log.info("accessToken: {}", accessToken);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getRole());
        log.info("refreshToken: {}", refreshToken);
        redisTemplate.opsForValue().set(user.getEmail(), refreshToken, 240, TimeUnit.HOURS);

        // 헤더에 실어보낼 정보
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addHeader("email", user.getEmail());
        response.addHeader("nickName", user.getNickName());
        response.addHeader("role", user.getRole());
        return response;
    }

    public User signUp(UserDto dto)  {
        log.info("signUp");
        if(userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email " + dto.getEmail() + " already exists");
        }
        if(userRepository.findByNickName(dto.getNickName()).isPresent()) {
            throw new IllegalArgumentException("Nickname " + dto.getNickName() + " already exists");
        }
        User user = dto.toEntity(encoder);
        log.info("Save user: {}", user);
        userRepository.save(user);
        return user;
    }

    //회원정보 수정
    public User modify(UserDto dto, HttpServletRequest request)  {
        log.info("modify");
        String email = request.getHeader("email");
        User user = userRepository.findByEmail(email).orElseThrow(()->
                new EntityNotFoundException("User with email " + email + " not found"));
        if(userRepository.findByEmail(dto.getNickName()).isPresent()) {
            throw new IllegalArgumentException("User with nickName " + dto.getNickName() + " already exists");
        }
        if(!dto.getNickName().isEmpty()){
            user.setNickName(dto.getNickName());
        }
        if(!dto.getPassword().isEmpty()){
            user.setPassword(encoder.encode(dto.getPassword()));
        }
        userRepository.save(user);
        log.info("Modify user: {}", user);
        return user;
    }

    public void delete(HttpServletRequest request)  {
        log.info("delete");
        String email = request.getHeader("email");
        User user = userRepository.findByEmail(email).orElseThrow(()->
                new EntityNotFoundException("User with email " + email + " not found"));
        userRepository.delete(user);
        redisTemplate.delete(user.getEmail());
    }

    public List<UserResDto> userList()  {
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

    public HttpServletResponse refresh(Map<String, String> email, HttpServletResponse response)  {
        log.info("refresh");
        User user = userRepository.findByEmail(email.get("email")).orElseThrow(()->
                new EntityNotFoundException("User with email " + email.get("email") + " not found"));

        Object refreshToken = redisTemplate.opsForValue().get(email.get("email"));
        log.info("refresh token: {}", refreshToken);
        if(refreshToken != null){
            String newAccessToken = jwtTokenProvider.createToken(user.getEmail(),user.getRole());
            response.addHeader("Authorization", "Bearer " + newAccessToken);
        }
        return response;
    }
}
