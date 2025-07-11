package com.example.hotdeal.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .additionalInterceptors((request, body, execution) -> {
                    // 현재 요청의 Authorization 헤더를 복사해서 내부 API 호출에 전달
                    RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
                    if (attrs instanceof ServletRequestAttributes) {
                        HttpServletRequest httpRequest = ((ServletRequestAttributes) attrs).getRequest();
                        String authHeader = httpRequest.getHeader("Authorization");
                        if (authHeader != null) {
                            request.getHeaders().set("Authorization", authHeader);
                            log.debug("JWT 토큰 전달 - URL: {}, Token: {}...",
                                    request.getURI(),
                                    authHeader.substring(0, Math.min(authHeader.length(), 20)));
                        } else {
                            log.debug("Authorization 헤더 없음 - URL: {}", request.getURI());
                        }
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }


}