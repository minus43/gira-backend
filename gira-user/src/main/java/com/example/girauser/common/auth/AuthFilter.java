package com.example.girauser.common.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String email = request.getHeader("email");
        String role = request.getHeader("role");
        log.info("email: {}, role: {}", email, role);
        TokenUserInfo userInfo = new TokenUserInfo(email,role);

        // spring security에게 전달할 인가 정보 리스트를 생성. (권한 정보)
        // 권한이 여러 개 존재할 경우 리스트로 권한 체크에 사용할 필드를 add. (권한 여러개면 여러번 add 가능)
        // 나중에 컨트롤러의 요청 메서드마다 권한을 파악하게 하기 위해 미리 저장을 해 놓는 것.
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        // ROLE_USER, ROLE_ADMIN (ROLE_ 접두사는 필수입니다.)
        authorityList.add(new SimpleGrantedAuthority("ROLE_" + role));
        log.info("authorities: {}", authorityList);

        // spring security에게 인증 정보를 전달해서 전역적으로 어플리케이션 내에서
        // 인증 정보를 활용할 수 있도록 설정.
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userInfo, // 컨트롤러 등에서 활용할 유저 정보
                "", // 인증된 사용자 비밀번호: 보통 null 혹은 빈 문자열로 선언.
                authorityList // 인가 정보 (권한)
        );
        log.info("authenticated: {}", auth);

        // 시큐리티 컨테이너에 인증 정보 객체 등록
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 다음 필터로 요청을 넘긴다.
        filterChain.doFilter(request, response);
    }
}
@Setter@Getter@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
class TokenUserInfo{
    private String email;
    private String role;
}
