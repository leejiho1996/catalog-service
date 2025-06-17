package com.polarbookshop.catalogservice.web;

import com.polarbookshop.catalogservice.domain.BookNotFoundException;
import com.polarbookshop.catalogservice.domain.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class) // MVC에 컴포넌트에 중점을 두고, BookController를 타깃으로 하는 테스트임을 명시
class BookControllerMvcTest {

    // 웹 계층을 테스트하기 위한 유틸리티 클래스
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService; // BookService Mock 객체, Controller에 주입해준다.

    @Test
    void whenGetBookNotExistingThenShouldReturn404() throws Exception {
        String isbn = "7373737313940";

        // BookService Mock이 어떻게 동작할 것인지 정의
        given(bookService.viewBookDetails(isbn))
                .willThrow(BookNotFoundException.class);

        mockMvc.perform(get("/books/" + isbn))
                .andExpect(status().isNotFound());
    }


}