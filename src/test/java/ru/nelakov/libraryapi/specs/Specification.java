package ru.nelakov.libraryapi.specs;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;
import ru.nelakov.libraryapi.listeners.CustomAllureListener;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

/**
 * Shared REST-assured setup. One immutable base request spec is built via {@link RequestSpecBuilder}
 * and registered as the global {@code RestAssured.requestSpecification}; tests call {@code given()},
 * which yields a fresh request per call, so no request state is mutated across tests. Full
 * request/response is logged only when an assertion fails, keeping green runs quiet.
 *
 * <p>The base specs for both test classes are identical, so re-assigning the global spec in each
 * subclass's {@code @BeforeAll} is idempotent. If per-class specs ever diverge, switch to
 * {@code given().spec(perClassSpec)} to stay parallel-safe.
 */
public abstract class Specification {

    protected static final String BASE_URI = "http://localhost:8080";

    @BeforeAll
    static void configureRestAssured() {
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .setContentType(ContentType.JSON)
                .addFilter(CustomAllureListener.withCustomTemplates())
                .build();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
    }

    protected static ResponseSpecification ok() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .build();
    }

    protected static ResponseSpecification created() {
        return new ResponseSpecBuilder()
                .expectStatusCode(201)
                .expectContentType(ContentType.JSON)
                .build();
    }

    /**
     * RFC 9457 ProblemDetail expectation: status line, {@code application/problem+json} media type,
     * and the {@code status} body field all agree. Centralizing the media-type check here pins the
     * RFC 9457 contract for every error path (404 / 409 / 400).
     */
    protected static ResponseSpecification problemDetail(int status) {
        return new ResponseSpecBuilder()
                .expectStatusCode(status)
                .expectHeader("Content-Type", containsString("problem+json"))
                .expectBody("status", equalTo(status))
                .build();
    }
}
