package com.polarbookshop.catalogservice.web;

import com.polarbookshop.catalogservice.config.SecurityConfig;
import com.polarbookshop.catalogservice.domain.BookNotFoundException;
import com.polarbookshop.catalogservice.domain.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class) // MVC에 컴포넌트에 중점을 두고, BookController를 타깃으로 하는 테스트임을 명시
@Import(SecurityConfig.class)
class BookControllerMvcTest {

    // 웹 계층을 테스트하기 위한 유틸리티 클래스
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService; // BookService Mock 객체, Controller에 주입해준다.

    @MockitoBean
    JwtDecoder jwtDecoder;

    @Test
    void whenGetBookNotExistingThenShouldReturn404() throws Exception {
        String isbn = "7373737313940";

        // BookService Mock이 어떻게 동작할 것인지 정의
        given(bookService.viewBookDetails(isbn))
                .willThrow(BookNotFoundException.class);

        mockMvc.perform(get("/books/" + isbn))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenDeleteBookWithEmployeeRoleThenShouldReturn204() throws Exception {
        String isbn = "1234567891";
        // employee 역할을 갖는 사용자에 대한 JWT 형식의 mock 엑세스 토큰을 HTTP에 추가
        mockMvc
                .perform(MockMvcRequestBuilders.delete("/books/" + isbn)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_employee"))))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void whenDeleteBookWithCustomerRoleThenShouldReturn403() throws Exception {
        String isbn = "1234567891";
        // customer 역할을 갖는 사용자에 대한 JWT 형식의 mock 엑세스 토큰을 HTTP에 추가
        mockMvc
                .perform(MockMvcRequestBuilders.delete("/books/" + isbn)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_customer"))))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void whenDeleteBookNotAuthenticatedThenShouldReturn401() throws Exception {
        String isbn = "1234567891";
        // customer 역할을 갖는 사용자에 대한 JWT 형식의 mock 엑세스 토큰을 HTTP에 추가
        mockMvc
                .perform(MockMvcRequestBuilders.delete("/books/" + isbn))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}