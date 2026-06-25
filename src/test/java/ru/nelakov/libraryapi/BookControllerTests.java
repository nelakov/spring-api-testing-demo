package ru.nelakov.libraryapi;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.nelakov.libraryapi.domain.Book;
import ru.nelakov.libraryapi.dto.BookRequest;
import ru.nelakov.libraryapi.specs.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@Feature("Books")
class BookControllerTests extends Specification {

    @Test
    @Story("List books")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /books returns all seeded books and matches the schema")
    void getAll_returnsSeededBooks_matchingSchema() {
        List<Book> books = given()
                .when().get("/api/v1/books")
                .then().spec(ok())
                .body(matchesJsonSchemaInClasspath("schemas/books.json"))
                .extract().as(new TypeRef<List<Book>>() {});

        assertThat(books)
                .extracting(Book::bookName)
                .contains("Tom Soyer", "The Island treasures", "VIY");
    }

    @Test
    @Story("Filter books by author")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /books?author=Mark Tven returns only that author's books")
    void getByAuthor_existing_returnsOnlyThatAuthor() {
        List<Book> books = given()
                .queryParam("author", "Mark Tven")
                .when().get("/api/v1/books")
                .then().spec(ok())
                .extract().as(new TypeRef<List<Book>>() {});

        assertThat(books).isNotEmpty()
                .allSatisfy(book -> assertThat(book.author()).isEqualTo("Mark Tven"));
        assertThat(books).extracting(Book::bookName).contains("Tom Soyer");
    }

    @Test
    @Story("Filter books by author")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /books?author=<unknown> returns 200 and an empty list")
    void getByAuthor_unknown_returnsEmptyList() {
        List<Book> books = given()
                .queryParam("author", "No Such Author")
                .when().get("/api/v1/books")
                .then().spec(ok())
                .extract().as(new TypeRef<List<Book>>() {});

        assertThat(books).isEmpty();
    }

    @Test
    @Story("Get book by name")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /books/{name} (unknown) -> 404 ProblemDetail")
    void getByName_unknown_returns404Problem() {
        given()
                .when().get("/api/v1/books/{name}", "No Such Book")
                .then().spec(problemDetail(404))
                .body("detail", equalTo("Book 'No Such Book' not found"));
    }

    @Test
    @Story("Create book")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /books creates a book, emits snake_case wire format, returns 201")
    void create_newBook_returns201_withSnakeCaseWireFormat() {
        String bookName = "Java " + UUID.randomUUID();
        BookRequest request =
                new BookRequest("about Java", "Joshua Bloch", bookName, 900, LocalDate.of(2018, 1, 1));

        Book created = given()
                .body(request)
                .when().post("/api/v1/books")
                .then().spec(created())
                .header("Location", containsString("/api/v1/books/"))
                // Assert the RAW wire keys so a camelCase regression (book_name -> bookName,
                // publish_date -> publishDate) would actually fail — and that LocalDate is ISO.
                .body("book_name", equalTo(bookName))
                .body("publish_date", equalTo("2018-01-01"))
                .extract().as(Book.class);

        assertThat(created.bookName()).isEqualTo(bookName);
        assertThat(created.author()).isEqualTo("Joshua Bloch");
        assertThat(created.pages()).isEqualTo(900);
        assertThat(created.publishDate()).isEqualTo(LocalDate.of(2018, 1, 1));
    }

    @Test
    @Story("Create book")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("POST /books with an existing book name -> 409 Conflict")
    void create_duplicate_returns409() {
        given()
                .body(new BookRequest("Tale", "Nikolai Gogol", "VIY", 450, LocalDate.of(1835, 1, 1)))
                .when().post("/api/v1/books")
                .then().spec(problemDetail(409))
                .body("detail", equalTo("Book 'VIY' already exists"));
    }

    @Test
    @Story("Create book")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("POST /books with invalid fields (blank title, non-positive pages, future date) -> 400")
    void create_invalid_returns400() {
        BookRequest invalid = new BookRequest(
                "", "Some Author", "Some Book " + UUID.randomUUID(), 0, LocalDate.now().plusDays(1));

        given()
                .body(invalid)
                .when().post("/api/v1/books")
                .then().spec(problemDetail(400));
    }
}
