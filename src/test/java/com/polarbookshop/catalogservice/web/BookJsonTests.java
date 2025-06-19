package com.polarbookshop.catalogservice.web;

import com.polarbookshop.catalogservice.domain.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest // JSON 직렬화에 중점을 둔 테스트 (JacksonTester도 구성해 준다)
public class BookJsonTests {

    @Autowired
    private JacksonTester<Book> json;

    @Test
    @DisplayName("직렬화 테스트")
    void testSerialize() throws Exception {
        Book book = Book.of("1234567890", "title", "author", 9.90, "Polarsophia");

        JsonContent<Book> jsonContent = json.write(book); // json으로 직렬화

        assertThat(jsonContent).extractingJsonPathStringValue("@.isbn")
                .isEqualTo(book.isbn());

        assertThat(jsonContent).extractingJsonPathStringValue("@.title")
                .isEqualTo(book.title());

        assertThat(jsonContent).extractingJsonPathStringValue("@.author")
                .isEqualTo(book.author());

        assertThat(jsonContent).extractingJsonPathNumberValue("@.price")
                .isEqualTo(book.price());

    }

    @Test
    @DisplayName("역직렬화 테스트")
    void testDeserialize() throws Exception {
        String content = """
                {
                "isbn": "1234567890",
                "title": "Title",
                "author": "Author",
                "price": 9.90,
                "publisher": "Polarsophia"
                }
                """;

        assertThat(json.parse(content))
                .usingRecursiveComparison() // 재귀적으로 필드의 값들을 비교
                .isEqualTo(Book.of("1234567890", "Title", "Author", 9.90, "Polarsophia"));
    }
}
