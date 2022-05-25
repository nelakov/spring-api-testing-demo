package ru.nelakov.springdemoTest;

import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.nelakov.springdemolibrarywithapitests.domain.Authors;

import java.util.Optional;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static listeners.CustomAllureListener.withCustomTemplates;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;

public class AuthorsControllerTests {

    @Test
    @DisplayName("Get all authors test")
    @Story("Authors")
    void authorsGetAllTest() {
        Authors[] authors =
                given()
                        .filter(withCustomTemplates())
                        .when()
                        .get("authors/getAllAuthors")
                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract()
                        .as(Authors[].class);

        Optional<Authors> first = Stream.of(authors)
                .filter(a -> a.getAuthorName().equals("Mark Tven"))
                .findFirst();
        assertThat(first.get().getAuthorName()).isEqualTo("Mark Tven");
    }

    static Stream<Arguments> argumentsForPutAuthor() {
        return Stream.of(
                Arguments.of(Authors.builder().authorName("Bruce Ekkel").build()),
                Arguments.of(Authors.builder().authorName("Joshua Bloch").build()),
                Arguments.of(Authors.builder().authorName("Cay Hortsmann").build())
        );
    }


    @Story("Authors")
    @MethodSource(value = "argumentsForPutAuthor")
    @ParameterizedTest(name = "Check put author to the authors list \"{0}\"")
    void putAuthorTest(Authors author) {
        Authors authors =
                given()
                        .filter(withCustomTemplates())
                        .contentType(JSON)
                        .body(author)
                        .when()
                        .put("authors/putAuthor")
                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract()
                        .as(Authors.class);

        assertThat(authors.getAuthorName()).isEqualTo(author.getAuthorName());
    }

    static Stream<Arguments> argumentsForGetAuthorByNamePositive() {
        return Stream.of(
                Arguments.of(Authors.builder().authorName("Mark Tven").build()),
                Arguments.of(Authors.builder().authorName("Nikolai Gogol").build())
        );
    }

    @Story("Authors")
    @MethodSource(value = "argumentsForGetAuthorByNamePositive")
    @ParameterizedTest(name = "Check search and get author for name \"{0}\"")
    void getAuthorByNamePositive(Authors author) {
        Authors authors =
                given()
                        .filter(withCustomTemplates())
                        .body(author)
                        .contentType(JSON)
                        .when()
                        .post("authors/getAuthor")
                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract()
                        .as(Authors.class);

        assertThat(authors.getAuthorName()).isEqualTo(author.getAuthorName());
    }

    static Stream<Arguments> argumentsForGetAuthorByNameNegative() {
        return Stream.of(
                Arguments.of(Authors.builder().authorName("Whi").build()),
                Arguments.of(Authors.builder().authorName("Who Author").build())
        );
    }

    @Story("Authors")
    @MethodSource(value = "argumentsForGetAuthorByNameNegative")
    @ParameterizedTest(name = "Check receive error when get author by non-exist name \"{0}\"")
    void getAuthorByNameNegative(Authors author) {
        Authors authors =
                given()
                        .filter(withCustomTemplates())
                        .body(author)
                        .contentType(JSON)
                        .when()
                        .post("authors/getAuthor")
                        .then()
                        .log().body()
                        .statusCode(404)
                        //.body("error", is("Not Found"))
                        .extract().as(Authors.class);
    }
}
