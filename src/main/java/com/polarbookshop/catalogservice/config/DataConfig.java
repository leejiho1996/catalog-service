package com.polarbookshop.catalogservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.Optional;

@Configuration
@EnableJdbcAuditing // CreatedDate, LastModifiedDate 사용 가능
public class DataConfig {

    /**
     * 현재 인증된 사용자의 이름을 반환하여 엔티티에 CreatedBy, LastModifiedBy 사용 가능
     */
    @Bean
    AuditorAware<String> auditorAware() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Principal::getName);
    }
}
