package ru.nelakov.springdemolibrarywithapitests.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.nelakov.springdemolibrarywithapitests.domain.Authors;
import ru.nelakov.springdemolibrarywithapitests.exception.InvalidAuthorException;
import ru.nelakov.springdemolibrarywithapitests.exception.NullAuthorException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AuthorsController {
    private List<Authors> authors = List.of(
            Authors.builder().authorName("Mark Tven").build(),
            Authors.builder().authorName("Leva Tolstoy").build(),
            Authors.builder().authorName("Fedor Dostoevskiy").build(),
            Authors.builder().authorName("Nikolai Gogol").build()
    );

    @GetMapping("authors/getAllAuthors")
    @Operation(summary = "Get all authors")
    public List<Authors> getAllAuthors() {
        List<Authors> result = new ArrayList<>();
        for (Authors authorOutput : authors) {
            result.add(authorOutput);
        }
        return result;
    }

    @PostMapping("authors/getAuthor")
    @Operation(summary = "Get author")
    public Authors getAuthor(@RequestBody Authors author) {
        if (author == null) {
            throw new NullAuthorException();
        }
        for (Authors oneOfAuthors : authors) {
            if (oneOfAuthors.equals(author)) {
                return oneOfAuthors;
            }
        }
        throw new InvalidAuthorException(HttpStatus.NOT_FOUND);
    }

    @PutMapping("authors/putAuthor")
    @Operation(summary = "Put author")
    public Authors putAuthor(@RequestBody Authors newAuthor) {
        return Authors.builder()
                .authorName(newAuthor.getAuthorName())
                .build();
    }

}
