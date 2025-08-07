package com.polarbookshop.catalogservice.domain;

import com.polarbookshop.catalogservice.config.DataConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@Import(DataConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
public class BookRepositoryJdbcTests {

    @Autowired
    private BookRepository bookRepository;

    // 데이터 베이스와 상호작용하기 위한 하위 수준의 객체
    // JPA의 경우 TestEntityManager
    @Autowired
    private JdbcAggregateTemplate jdbcAggregateTemplate;

    @Test
    void findBookByIsbnHenExisting() {
        String isbn = "1234561237";
        Book book = Book.of(isbn, "title", "author", 12.90, "Polarsophia");
        jdbcAggregateTemplate.insert(book);

        Optional<Book> byIsbn = bookRepository.findByIsbn(isbn);

        assertThat(byIsbn).isPresent();
        assertThat(byIsbn.get().isbn()).isEqualTo(isbn);

    }

    @Test
    void whenCreateBookNotAuthenticatedThenNoAuditMetadata() {
        Book book = Book.of("1234561237", "title", "author", 12.90, "Polarsophia");
        Book createdBook = bookRepository.save(book);

        assertThat(createdBook.createdBy()).isNull();
        assertThat(createdBook.lastModifiedBy()).isNull();
    }

    @Test
    @WithMockUser("john")
    void whenCreateBookAuthenticatedThenAuditMetadata() {
        Book book = Book.of("1234561237", "title", "author", 12.90, "Polarsophia");
        Book createdBook = bookRepository.save(book);

        assertThat(createdBook.createdBy()).isEqualTo("john");
        assertThat(createdBook.lastModifiedBy()).isEqualTo("john");
    }

}
