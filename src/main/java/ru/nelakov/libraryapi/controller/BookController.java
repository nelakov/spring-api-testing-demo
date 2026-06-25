package ru.nelakov.libraryapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.nelakov.libraryapi.domain.Book;
import ru.nelakov.libraryapi.dto.BookRequest;
import ru.nelakov.libraryapi.service.BookService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/books", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List books, optionally filtered by author")
    public List<Book> getBooks(@RequestParam(required = false) String author) {
        return author == null ? service.getAll() : service.getByAuthor(author);
    }

    @GetMapping("/{bookName}")
    @Operation(summary = "Get a book by its name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found"),
            @ApiResponse(responseCode = "404", description = "Book not found (application/problem+json)")
    })
    public Book getByBookName(@PathVariable String bookName) {
        return service.getByBookName(bookName);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a book")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created; Location points at the new book"),
            @ApiResponse(responseCode = "400", description = "Validation failed (application/problem+json)"),
            @ApiResponse(responseCode = "409", description = "Book already exists (application/problem+json)")
    })
    public ResponseEntity<Book> create(@Valid @RequestBody BookRequest request,
                                       UriComponentsBuilder uriBuilder) {
        Book created = service.create(new Book(
                request.title(),
                request.author(),
                request.bookName(),
                request.pages(),
                request.publishDate()));
        URI location = uriBuilder.path("/api/v1/books/{bookName}")
                .buildAndExpand(created.bookName())
                .encode()
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
}
