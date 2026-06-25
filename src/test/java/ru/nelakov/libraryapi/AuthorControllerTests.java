package ru.nelakov.libraryapi;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.nelakov.libraryapi.domain.Author;
import ru.nelakov.libraryapi.dto.AuthorRequest;
import ru.nelakov.libraryapi.specs.Specification;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@Feature("Authors")
class AuthorControllerTests extends Specification {

    @Test
    @Story("List authors")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /authors returns the seeded authors and matches the schema")
    void getAll_returnsSeededAuthors_matchingSchema() {
        List<Author> authors = given()
                .when().get("/api/v1/authors")
                .then().spec(ok())
                .body(matchesJsonSchemaInClasspath("schemas/authors.json"))
                .extract().as(new TypeRef<List<Author>>() {});

        assertThat(authors)
                .extracting(Author::name)
                .contains("Mark Tven", "Leva Tolstoy", "Fedor Dostoevskiy", "Nikolai Gogol");
    }

    @ParameterizedTest(name = "GET /authors/{0} returns that author")
    @ValueSource(strings = {"Mark Tven", "Nikolai Gogol"})
    @Story("Get author by name")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /authors/{name} returns an existing author")
    void getByName_existing_returnsAuthor(String name) {
        Author author = given()
                .when().get("/api/v1/authors/{name}", name)
                .then().spec(ok())
                .extract().as(Author.class);

        assertThat(author.name()).isEqualTo(name);
    }

    @ParameterizedTest(name = "GET /authors/{0} (unknown) -> 404 ProblemDetail")
    @ValueSource(strings = {"Whi", "Who Author"})
    @Story("Get author by name")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /authors/{name} for an unknown author returns 404 ProblemDetail")
    void getByName_unknown_returns404Problem(String name) {
        given()
                .when().get("/api/v1/authors/{name}", name)
                .then().spec(problemDetail(404))
                .body("detail", equalTo("Author '%s' not found".formatted(name)));
    }

    @Test
    @Story("Create author")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /authors creates an author and returns 201 + Location")
    void create_newAuthor_returns201_andIsRetrievable() {
        String name = "Author " + UUID.randomUUID();

        Author created = given()
                .body(new AuthorRequest(name))
                .when().post("/api/v1/authors")
                .then().spec(created())
                .header("Location", containsString("/api/v1/authors/"))
                .extract().as(Author.class);

        assertThat(created.name()).isEqualTo(name);

        given()
                .when().get("/api/v1/authors/{name}", name)
                .then().spec(ok())
                .body("name", equalTo(name));
    }

    @Test
    @Story("Create author")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("POST /authors with an existing name -> 409 Conflict")
    void create_duplicate_returns409() {
        given()
                .body(new AuthorRequest("Mark Tven"))
                .when().post("/api/v1/authors")
                .then().spec(problemDetail(409))
                .body("detail", equalTo("Author 'Mark Tven' already exists"));
    }

    @Test
    @Story("Create author")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("POST /authors with a blank name -> 400 Bad Request")
    void create_blankName_returns400() {
        given()
                .body(new AuthorRequest(""))
                .when().post("/api/v1/authors")
                .then().spec(problemDetail(400));
    }

    @Test
    @Story("Create author")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("POST /authors with a slash in the name -> 400 Bad Request")
    void create_nameWithSlash_returns400() {
        given()
                .body(new AuthorRequest("Foo/Bar"))
                .when().post("/api/v1/authors")
                .then().spec(problemDetail(400));
    }
}
