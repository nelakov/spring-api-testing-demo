package specs;

import io.restassured.specification.RequestSpecification;
import listeners.CustomAllureListener;

import static io.restassured.RestAssured.with;

public class Specification {
    public static RequestSpecification booksRequestSpec =
            with()
                    .baseUri("http://localhost:8080")
                    .basePath("/books");
                   //.filter(CustomAllureListener.withCustomTemplates());

    public static RequestSpecification authorsRequestSpec =
            with()
                    .baseUri("http://localhost:8080")
                    .basePath("/authors");
                    //.filter(CustomAllureListener.withCustomTemplates());
}
