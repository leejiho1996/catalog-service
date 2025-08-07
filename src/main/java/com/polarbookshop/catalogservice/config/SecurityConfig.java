package com.polarbookshop.catalogservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/books/**", "/").permitAll()
                        // hasAuthorities() 를 사용하면 SCOPE_ 등 모든 유형의 권한 확인 가능
                        // hasRole을 쓰면 내부적으로 ROLE_ 접두어를 붙혀줌
                        .anyRequest().hasRole("employee")

                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())) // JWT인증을 하도록 지정
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 각 요청에 대해 세션 생성을 하지않아 무상태를 유지
                .csrf(AbstractHttpConfigurer::disable) // 인증이 상태를 가지지 않고, 브라우저 기반 클라이언트가 관여하지 않기 때문에 비활성화
                .build();
    }

    /**
     * 이 Bean을 따로 정의하지 않을 시 SCOPE를 기반으로 역할을 추출한다.
     * SCOPE 기반이 아닌 ROLE 기반으로 역할을 사용할 것이니 해당 내용을 정의해준다.
     */
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_"); // 각각의 역할에 ROLE_을 접두어로 사용
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles"); // roles 클레임에서 역할 추출

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
}
