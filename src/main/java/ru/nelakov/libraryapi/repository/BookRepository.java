package ru.nelakov.libraryapi.repository;

import org.springframework.stereotype.Repository;
import ru.nelakov.libraryapi.domain.Book;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory book store keyed by book name, which is the unique business identifier
 * (duplicate book names are rejected by the service with 409). Concrete by design
 * (single implementation, no second consumer) — an interface would be premature (YAGNI).
 */
@Repository
public class BookRepository {

    private final ConcurrentMap<String, Book> booksByName = new ConcurrentHashMap<>();

    public BookRepository() {
        saveIfAbsent(new Book("Story", "Mark Tven", "Tom Soyer", 250, LocalDate.of(1876, 6, 1)));
        saveIfAbsent(new Book("Story", "Robert Stivenson", "The Island treasures", 399, LocalDate.of(1883, 1, 1)));
        saveIfAbsent(new Book("Tale", "Nikolai Gogol", "VIY", 450, LocalDate.of(1835, 1, 1)));
    }

    public List<Book> findAll() {
        return List.copyOf(booksByName.values());
    }

    public List<Book> findByAuthor(String author) {
        return booksByName.values().stream()
                .filter(book -> book.author().equals(author))
                .toList();
    }

    public Optional<Book> findByBookName(String bookName) {
        return Optional.ofNullable(booksByName.get(bookName));
    }

    /**
     * Atomic insert-if-absent. Returns the book already stored under this name (a conflict),
     * or empty if the insert succeeded — collapsing the check-then-act into one atomic step.
     */
    public Optional<Book> saveIfAbsent(Book book) {
        return Optional.ofNullable(booksByName.putIfAbsent(book.bookName(), book));
    }
}
