package com.example.apigatewayservice.exception;

public class UnAuthenticationException extends RuntimeException {

    public UnAuthenticationException() {
        super("인증이 필요한 서비스 입니다.");
    }

    public UnAuthenticationException(String message) {
        super(message);
    }
}
