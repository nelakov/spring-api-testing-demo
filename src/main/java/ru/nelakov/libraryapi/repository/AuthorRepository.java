package ru.nelakov.libraryapi.repository;

import org.springframework.stereotype.Repository;
import ru.nelakov.libraryapi.domain.Author;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

/**
 * In-memory author store keyed by name. Concrete (not an interface) by design: single
 * implementation, no second consumer — an interface would be premature (YAGNI). Extract one
 * only if a persistent implementation is added.
 */
@Repository
public class AuthorRepository {

    private final ConcurrentMap<String, Author> authorsByName = new ConcurrentHashMap<>();

    public AuthorRepository() {
        Stream.of("Mark Tven", "Leva Tolstoy", "Fedor Dostoevskiy", "Nikolai Gogol")
                .map(Author::new)
                .forEach(this::saveIfAbsent);
    }

    public List<Author> findAll() {
        return List.copyOf(authorsByName.values());
    }

    public Optional<Author> findByName(String name) {
        return Optional.ofNullable(authorsByName.get(name));
    }

    /**
     * Atomic insert-if-absent. Returns the author already stored under this name (a conflict),
     * or empty if the insert succeeded — collapsing the check-then-act into one atomic step.
     */
    public Optional<Author> saveIfAbsent(Author author) {
        return Optional.ofNullable(authorsByName.putIfAbsent(author.name(), author));
    }
}
