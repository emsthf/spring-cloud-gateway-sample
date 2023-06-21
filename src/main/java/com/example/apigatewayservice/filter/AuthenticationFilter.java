package com.example.apigatewayservice.filter;

import com.example.apigatewayservice.exception.UnAuthenticationException;
import io.jsonwebtoken.Jwts;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 특정 서비스만 사용할 인증 Filter
 *
 * 1. AbstractGatewayFilterFactory 상속
 * 2. innser class로 Config 정의 - configuration properties member variable
 * 3. default 생성자 만들어서 super로 부모에게 Config.class 전달
 * 4. apply 메소드 재정의 - request token check Filter 작성
 */
@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    /**
     * 상속하고 있는 부모 AbstractGatewayFilterFactory에게 Config.class를 넘겨준다.
     */
    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            log.info("Authentication Filter start");

            String accessToken = request.getHeaders().getFirst("access-token");
            String userId = request.getHeaders().getFirst("userId");

            // 토큰 검증
            String subject = Jwts.parser().setSigningKey(config.getSecret())
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .getSubject();

            if (accessToken == null || userId == null ||
                    accessToken.isEmpty() || userId.isEmpty() || !subject.equals(userId)) {
                log.error("유효하지 않은 토큰");
//                throw new UnAuthenticationException();
                return onError(exchange, "JWT is not valid", HttpStatus.UNAUTHORIZED);
            }

            // access-token은 이미 여기서 검증이 끝났으므로 검증이 끝났다는 의미에서 header에 userId만 담아서 전달하자.
            request.mutate().header("userId", subject).build();
            log.info("Authentication Filter end");

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String errorMsg, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error("onError: {}", errorMsg);
        return response.setComplete();
    }

    /**
     * yaml 파일에서 설정한 값이 여기로 들어온다. 그러므로 yaml의 값과 이름이 같아야 한다.
     */
    @Data
    public static class Config {
        private String secret;
    }
}
