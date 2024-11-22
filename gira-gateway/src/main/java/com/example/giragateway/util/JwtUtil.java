package com.example.giragateway.util;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class JwtUtil {
    @Value("${jwt.secretKey}")
    private String secretKey;

    public String parseBearerToken(ServerHttpRequest request) {

        // 요청 헤더에서 토큰 꺼내오기
        // -- content-type: application/json
        // -- Authorization: Bearer aslkdblk2dnkln34kl52...
        String bearerToken = request.getHeaders().getFirst("Authorization");

        // 아직 순수 토큰값이 아닌 Bearer 이 붙어 있으니 이것을 제거하자.
        // StringUtils.hasText(문자열) -> null이거나 공백만 있거나 빈 문자열이면 false
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    public Claims validateToken(String token) throws Exception  {
        return Jwts.parserBuilder()
                // 토큰 발급자의 발급 당시의 서명을 넣어줌.
                .setSigningKey(secretKey)
                // 서명 위조 검사: 위조된 경우에는 예외가 발생합니다.
                // 위조되지 않았다면 payload를 리턴.
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public ServerHttpRequest addUserInfo(ServerHttpRequest request, Claims claims) {
        return request.mutate()
                .header("email", claims.getSubject())
                .header("role",claims.get("role", String.class))
                .build();
    }
}
