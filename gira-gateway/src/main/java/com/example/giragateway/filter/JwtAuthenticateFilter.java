package com.example.giragateway.filter;

import com.example.giragateway.config.PathConfig;
import com.example.giragateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;


@Component
@Slf4j
public class JwtAuthenticateFilter extends AbstractGatewayFilterFactory<JwtAuthenticateFilter.Config> {

    //이거 통과하는 경로가 2개 이상일 경우 존재, 수정 필요
    private final PathConfig pathConfig;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final JwtUtil jwtUtil;

    // Config 클래스 정의 (필요하면 설정값 추가 가능)
    public static class Config {
        // Example of a configurable field
    }

    public JwtAuthenticateFilter(PathConfig pathConfig, JwtUtil jwtUtil) {
        super(Config.class); // Config 클래스 설정
        this.pathConfig = pathConfig;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            // 인증이 필요 없는 경로 확인
            String path = exchange.getRequest().getURI().getPath();
            log.info("excluded path: {}", pathConfig.getExcludedPaths());
            log.info("path: {}", path);
            if (isExcludedPath(path)) {
                return chain.filter(exchange); // 필터 건너뛰기
            }

            //request 획득, 리퀘스트에서 토큰 획득
            ServerHttpRequest request = exchange.getRequest();
            String token = jwtUtil.parseBearerToken(request);


            try {
                // JWT 검증
                Claims claims= (Claims) jwtUtil.validateToken(token);
                //request에 email, role header에 추가
                ServerHttpRequest modifiedRequest = jwtUtil.addUserInfo(request, claims);

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                return onError(exchange,"Invalid Token", HttpStatus.UNAUTHORIZED);
            }
        };
    }
    private Mono<Void> onError(ServerWebExchange exchange, String msg, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error(msg);
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Flux.just(buffer));
    }

    private boolean isExcludedPath(String path) {
        return pathConfig.getExcludedPaths().stream().anyMatch(excluedPath -> path.equals(excluedPath));
    }
}
