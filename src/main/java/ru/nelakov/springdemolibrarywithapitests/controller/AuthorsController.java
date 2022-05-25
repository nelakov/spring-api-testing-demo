package ru.nelakov.springdemolibrarywithapitests.controller;

import io.swagger.annotations.ApiOperation;
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
    @ApiOperation("Get all authors")
    public List<Authors> getAllAuthors() {
        List<Authors> result = new ArrayList<>();
        for (Authors authorOutput : authors) {
            result.add(authorOutput);
        }
        return result;
    }

    @PostMapping("authors/getAuthor")
    @ApiOperation("Get author")
    public Authors getAuthor(@RequestBody Authors author) {
        Authors result = null;
        if (author == null) {
            throw new NullAuthorException();
        } else {
            for (Authors oneOfAuthors : authors) {
                if (oneOfAuthors.equals(author)) {
                    result = oneOfAuthors;
                } else {
                    throw new InvalidAuthorException(HttpStatus.NOT_FOUND);
                }
            }
        }
        return result;
    }

    @PutMapping("authors/putAuthor")
    @ApiOperation("Put author")
    public Authors putAuthor(@RequestBody Authors newAuthor) {
        return Authors.builder()
                .authorName(newAuthor.getAuthorName())
                .build();
    }

}
