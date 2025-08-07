package com.polarbookshop.catalogservice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.polarbookshop.catalogservice.domain.Book;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("integration")
@Testcontainers // 테스트 컨테이너 시작 중지 활성화
class CatalogServiceApplicationTests {

	// Customer
	private static KeycloakToken bjornTokens;
	// Customer and employee
	private static KeycloakToken isabelleTokens;


	// Rest 엔드포인트 호출할 유틸리티
	@Autowired
	private WebTestClient webClient;

	// 키클록 컨테이너 정의 (설정 json 파일은 test/resource 경로에 넣어준다)
	@Container
	private static final KeycloakContainer keycloakContainer =
			new KeycloakContainer("quay.io/keycloak/keycloak:24.0")
					.withRealmImportFile("test-realm-config.json");
    @Autowired
    private WebTestClient webTestClient;

	// 키클록 issuer-uri 가 테스트 키클록 인스턴스를 카리키도록 변경
	@DynamicPropertySource
	static void dynamicProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
				() -> keycloakContainer.getAuthServerUrl() + "/realms/PolarBookshop");
	}

	@BeforeAll
	static void generateAccessTokens() {
		WebClient webClient = WebClient.builder()
				.baseUrl(keycloakContainer.getAuthServerUrl() + "/realms/PolarBookshop/protocol/openid-connect/token")
				.defaultHeader(HttpHeaders.CONTENT_TYPE,
						MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.build();

		isabelleTokens = authenticateWith("isabelle", "password", webClient);
		bjornTokens = authenticateWith("bjorn", "password", webClient);
	}

	@Test
	@DisplayName("책 등록 테스트")
	void whenPostRequestThenBookCreated() {
		Book expectedBook = Book.of("1231231231", "TITLE", "AUTHOR", 9.90, "Polarsophia");

		webClient
				.post()
				.uri("/books")
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken)) // 헤더에 엑세스 토큰 추가
				.bodyValue(expectedBook)
				.exchange() // 요청 전송
				.expectStatus().isCreated() // 201 코드인지 확인
				.expectBody(Book.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
				});
	}

	private static KeycloakToken authenticateWith(String username, String password, WebClient webClient) {
		return webClient
				.post()
				.body( // 키클록과 직접 인증하기 위해 Password Grant 방식 사용
						BodyInserters.fromFormData("grant_type", "password")
								.with("client_id", "polar-test")
								.with("username", username)
								.with("password", password)
				)
				.retrieve()
				.bodyToMono(KeycloakToken.class)
				.block(); // 동기적으로 대기
	}

	private record KeycloakToken(String accessToken) {

		// 잭슨 라이브러리가 이 생성자를 통해 Json을 KeycloakToken 객체로 역질렬화하도록 함 (@JsonCreator 없어도 동작한다)
		@JsonCreator
		private KeycloakToken(@JsonProperty("access_token") String accessToken) {
			this.accessToken = accessToken;
		}
	}
}
