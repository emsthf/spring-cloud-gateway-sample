package com.example.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 로그는 모든 상황에서 남겨야 하므로 Global Filter로 만들어야 한다.
 *
 * 1. AbstractGatewayFilterFactory 상속
 * 2. innser class로 Config 정의 - configuration properties member variable
 * 3. default 생성자 만들어서 super로 부모에게 Config.class 전달
 * 4. apply 메소드 재정의 - request, response Filter 작성
 */
@Slf4j
@Component
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    /**
     * 상속하고 있는 부모 AbstractGatewayFilterFactory에게 Config.class를 넘겨준다.
     */
    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Global Filter baseMessage: {}", config.getBaseMessage());
            if (config.isPreLogger()) {
                log.info("Global Filter Start: request id -> {}", request.getId());
            }
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                    log.info("Global Filter End: response code -> {}", response.getStatusCode());
                }
            }));
        };

        // 람다를 사용하지 않고 아래와 같이 작성할 수도 있다. 하지만 람다를 사용한 위 코드가 더 가독성이 좋음
//        return new OrderedGatewayFilter((exchange, chain) -> {
//            ServerHttpRequest request = exchange.getRequest();
//            ServerHttpResponse response = exchange.getResponse();
//
//            log.info("loggingFilter baseMessage: {}", config.getBaseMessage());
//            if (config.isPreLogger()) {
//                log.info("loggingFilter pre filter: {}", request.getId());
//            }
//            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
//                if (config.isPostLogger()) {
//                    log.info("loggingFilter post filter: {}", response.getStatusCode());
//                }
//            }));
//        }, Ordered.LOWEST_PRECEDENCE);
    }

    /**
     * yaml 파일에서 설정한 값이 여기로 들어온다. 그러므로 yaml의 값과 이름이 같아야 한다.
     */
    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}
