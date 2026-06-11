package ru.nelakov.springdemolibrarywithapitests.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.nelakov.springdemolibrarywithapitests.domain.Authors;
import ru.nelakov.springdemolibrarywithapitests.domain.BooksInfo;
import ru.nelakov.springdemolibrarywithapitests.exception.InvalidAuthorException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.kv;


@RestController
public class LibraryController {
    private static final Logger log = LoggerFactory.getLogger(LibraryController.class);

    private List<BooksInfo> books = List.of(
            BooksInfo.builder().title("Story").author("Mark Tven").bookName("Tom Soyer").pages(250).publishDate(new Date(1234567)).build(),
            BooksInfo.builder().title("Story").author("Robert Stivenson").bookName("The Island treasures").pages(399).publishDate(new Date(1234567)).build(),
            BooksInfo.builder().title("Tale").author("Nikolai Gogol").bookName("VIY").pages(450).publishDate(new Date(1234567)).build()
    );


    @GetMapping("books/getAll")
    @Operation(summary = "Get all books in the Library")
    public List<BooksInfo> getAllBooks() {
        List<BooksInfo> result = new ArrayList<>();
        for (BooksInfo book : books) {
            result.add(book);
        }
        return result;
    }

    @PostMapping("books/getBookInfoListByAuthor")
    @Operation(summary = "Get books by Author")
    public List<BooksInfo> getBookInfoListByAuthor(@RequestBody Authors author) {
        List<BooksInfo> matches = books.stream()
                .filter(booksInfo -> booksInfo.getAuthor().equals(author.getAuthorName()))
                .collect(Collectors.toList());
        if (matches.isEmpty()) {
            log.warn("No books found for author", kv("author", author.getAuthorName()), kv("step", "author_lookup"));
            throw new InvalidAuthorException(HttpStatus.NOT_FOUND);
        }
        return matches;
    }

    @PostMapping("books/putBook")
    @Operation(summary = "Put book in the Library")
    public BooksInfo putBook(@RequestBody BooksInfo booksData) {
        return BooksInfo.builder()
                .title(booksData.getTitle())
                .author(booksData.getAuthor())
                .bookName(booksData.getBookName())
                .pages(booksData.getPages())
                .publishDate(booksData.getPublishDate())
                .build();
    }
}