package ru.nelakov.libraryapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.nelakov.libraryapi.domain.Book;
import ru.nelakov.libraryapi.exception.ResourceAlreadyExistsException;
import ru.nelakov.libraryapi.exception.ResourceNotFoundException;
import ru.nelakov.libraryapi.repository.BookRepository;

import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> getAll() {
        return repository.findAll();
    }

    public List<Book> getByAuthor(String author) {
        List<Book> books = repository.findByAuthor(author);
        if (books.isEmpty()) {
            log.debug("No books found for author", kv("author", author), kv("step", "author_filter"));
        }
        return books;
    }

    public Book getByBookName(String bookName) {
        return repository.findByBookName(bookName)
                .orElseThrow(() -> new ResourceNotFoundException("Book '%s' not found".formatted(bookName)));
    }

    public Book create(Book book) {
        // Atomic insert-if-absent: a non-empty result means the book name was already taken.
        if (repository.saveIfAbsent(book).isPresent()) {
            throw new ResourceAlreadyExistsException("Book '%s' already exists".formatted(book.bookName()));
        }
        log.info("Book created", kv("book", book.bookName()), kv("author", book.author()), kv("step", "book_create"));
        return book;
    }
}
