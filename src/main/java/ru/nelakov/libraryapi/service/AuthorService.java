package ru.nelakov.libraryapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.nelakov.libraryapi.domain.Author;
import ru.nelakov.libraryapi.exception.ResourceAlreadyExistsException;
import ru.nelakov.libraryapi.exception.ResourceNotFoundException;
import ru.nelakov.libraryapi.repository.AuthorRepository;

import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
public class AuthorService {

    private static final Logger log = LoggerFactory.getLogger(AuthorService.class);

    private final AuthorRepository repository;

    public AuthorService(AuthorRepository repository) {
        this.repository = repository;
    }

    public List<Author> getAll() {
        return repository.findAll();
    }

    public Author getByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Author '%s' not found".formatted(name)));
    }

    public Author create(Author author) {
        // Atomic insert-if-absent: a non-empty result means the name was already taken.
        if (repository.saveIfAbsent(author).isPresent()) {
            throw new ResourceAlreadyExistsException("Author '%s' already exists".formatted(author.name()));
        }
        log.info("Author created", kv("author", author.name()), kv("step", "author_create"));
        return author;
    }
}
