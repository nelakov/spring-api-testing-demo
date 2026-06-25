package ru.nelakov.libraryapi.specs;
import io.restassured.specification.RequestSpecification;
import ru.nelakov.libraryapi.listeners.CustomAllureListener;


import static io.restassured.RestAssured.with;
import static io.restassured.http.ContentType.JSON;

public class Specification {
   public static RequestSpecification booksRequestSpec =
            with()
                    .baseUri("http://localhost:8080")
                    .basePath("/books")
                    .contentType(JSON)
                    .log().uri()
                    .log().body()
                    .filter(CustomAllureListener.withCustomTemplates());

    public static RequestSpecification authorsRequestSpec =
            with()
                    .baseUri("http://localhost:8080")
                    .basePath("/authors")
                    .contentType(JSON)
                    .log().uri()
                    .log().body()
                    .filter(CustomAllureListener.withCustomTemplates());
}
