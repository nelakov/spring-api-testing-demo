package ru.nelakov.springdemoTest;

import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.nelakov.springdemolibrarywithapitests.domain.Authors;
import ru.nelakov.springdemolibrarywithapitests.domain.BooksInfo;
import specs.Specification;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.JSON;
import static listeners.CustomAllureListener.withCustomTemplates;
import static org.assertj.core.api.Assertions.assertThat;

public class LibraryControllerTests extends Specification {

    @Test
    @Story("Library")
    @DisplayName("Get all books in library test")
    void libraryGetAllTest() {
        BooksInfo[] booksInfos =
                booksRequestSpec
                        .when()
                        .get("/getAll")
                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract()
                        .as(BooksInfo[].class);

        Optional<BooksInfo> first = Stream.of(booksInfos)
                .filter(a -> a.getTitle().contains("Story"))
                .filter(a -> a.getAuthor().contains("Mark Tven"))
                .findFirst();
        assertThat(first.get().getTitle()).isEqualTo("Story");
        assertThat(first.get().getAuthor()).isEqualTo("Mark Tven");
        assertThat(first.get().getBookName()).isEqualTo("Tom Soyer");
        assertThat(first.get().getPages()).isEqualTo(250);
    }

    static Stream<Arguments> argumentsForPostBook() {
        return Stream.of(
                Arguments.of(Authors.builder().authorName("Mark Tven").build())
        );
    }

    @Story("Library")
    @MethodSource(value = "argumentsForPostBook")
    @ParameterizedTest(name = "Check books for existent author \"{0}\"")
    void getBookByAuthorTest(Authors authors) {
        BooksInfo[] booksInfos =
                booksRequestSpec
                        .body(authors)
                        .when()
                        .post("/getBookInfoListByAuthor")
                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract()
                        .as(BooksInfo[].class);

        Optional<BooksInfo> first = Stream.of(booksInfos)
                .filter(a -> a.getTitle().contains("Story"))
                .filter(a -> a.getAuthor().contains("Mark Tven"))
                .findFirst();
        assertThat(first.get().getTitle()).isEqualTo("Story");
        assertThat(first.get().getAuthor()).isEqualTo("Mark Tven");
        assertThat(first.get().getBookName()).isEqualTo("Tom Soyer");
        assertThat(first.get().getPages()).isEqualTo(250);
    }

    static Stream<Arguments> argumentsForPutBook() {
        return Stream.of(
                Arguments.of(BooksInfo.builder().title("about Java").author("Bruce Ekkel").bookName("Java").pages(900).publishDate(new Date()).build()),
                Arguments.of(BooksInfo.builder().title("about Java").author("Joshua Bloch").bookName("Java").pages(1800).publishDate(new Date()).build()),
                Arguments.of(BooksInfo.builder().title("about Java").author("Cay Hortsmann").bookName("Java").pages(2000).publishDate(new Date()).build())
        );
    }

    @Story("Library")
    @MethodSource(value = "argumentsForPutBook")
    @ParameterizedTest(name = "Check put books in library \"{0}\"")
    void putBookTest(BooksInfo booksInfo) {
        BooksInfo newBook =
                booksRequestSpec
                        .body(booksInfo)
                        .when()
                        .post("/putBook")
                        .then()
                        .statusCode(200)
                        .log().body()
                        .extract()
                        .response()
                        .as(BooksInfo.class);
        assertThat(newBook.getAuthor()).isEqualTo(booksInfo.getAuthor());
        assertThat(newBook.getBookName()).isEqualTo(booksInfo.getBookName());
        assertThat(newBook.getPages()).isEqualTo(booksInfo.getPages());
        assertThat(newBook.getTitle()).isEqualTo(booksInfo.getTitle());
    }

    static Stream<Arguments> argumentsForPostBookNegativeTest() {
        return Stream.of(
                Arguments.of(Authors.builder().authorName("Bruce Ekkel").build()),
                Arguments.of(Authors.builder().authorName("Joshua Bloch").build()),
                Arguments.of(Authors.builder().authorName("Cay Hortsmann").build())
        );
    }

    @Story("Library")
    @MethodSource(value = "argumentsForPostBookNegativeTest")
    @ParameterizedTest(name = "Check books for non-existent author \"{0}\"")
    void getBookByAuthorNegativeTest(Authors authors) {
        booksRequestSpec
                .body(authors)
                .when()
                .post("/getBookInfoListByAuthor")
                .then()
                .log().body()
                .statusCode(404);
    }
}


