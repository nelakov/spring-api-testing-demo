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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.nelakov.libraryapi.domain.Author;
import ru.nelakov.libraryapi.dto.AuthorRequest;
import ru.nelakov.libraryapi.service.AuthorService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/authors", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthorController {

    private final AuthorService service;

    public AuthorController(AuthorService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List all authors")
    public List<Author> getAll() {
        return service.getAll();
    }

    @GetMapping("/{name}")
    @Operation(summary = "Get an author by name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found"),
            @ApiResponse(responseCode = "404", description = "Author not found (application/problem+json)")
    })
    public Author getByName(@PathVariable String name) {
        return service.getByName(name);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create an author")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created; Location points at the new author"),
            @ApiResponse(responseCode = "400", description = "Validation failed (application/problem+json)"),
            @ApiResponse(responseCode = "409", description = "Author already exists (application/problem+json)")
    })
    public ResponseEntity<Author> create(@Valid @RequestBody AuthorRequest request,
                                         UriComponentsBuilder uriBuilder) {
        Author created = service.create(new Author(request.name()));
        URI location = uriBuilder.path("/api/v1/authors/{name}")
                .buildAndExpand(created.name())
                .encode()
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
}
