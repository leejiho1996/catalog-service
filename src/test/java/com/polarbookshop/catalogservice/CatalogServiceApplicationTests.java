package com.polarbookshop.catalogservice;

import com.polarbookshop.catalogservice.domain.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class CatalogServiceApplicationTests {

	// Rest 엔드포인트 호출할 유틸리티
	@Autowired
	private WebTestClient webClient;

	@Test
	@DisplayName("책 등록 테스트")
	void whenPostRequestThenBookCreated() {
		Book expectedBook = Book.of("1231231231", "TITLE", "AUTHOR", 9.90);

		webClient
				.post()
				.uri("/books")
				.bodyValue(expectedBook)
				.exchange() // 요청 전송
				.expectStatus().isCreated() // 201 코드인지 확인
				.expectBody(Book.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
				});
	}

}
