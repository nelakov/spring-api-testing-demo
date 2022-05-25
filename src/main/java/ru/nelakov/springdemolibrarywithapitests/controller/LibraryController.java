package ru.nelakov.springdemolibrarywithapitests.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.nelakov.springdemolibrarywithapitests.domain.Authors;
import ru.nelakov.springdemolibrarywithapitests.domain.BooksInfo;
import ru.nelakov.springdemolibrarywithapitests.exception.InvalidAuthorException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class LibraryController {
    private List<BooksInfo> books = List.of(
            BooksInfo.builder().title("Story").author("Mark Tven").bookName("Tom Soyer").pages(250).publishDate(new Date(1234567)).build(),
            BooksInfo.builder().title("Story").author("Robert Stivenson").bookName("The Island treasures").pages(399).publishDate(new Date(1234567)).build(),
            BooksInfo.builder().title("Tale").author("Nikolai Gogol").bookName("VIY").pages(450).publishDate(new Date(1234567)).build()
    );


    @GetMapping("books/getAll")
    @ApiOperation("Get all books in the Library")
    public List<BooksInfo> getAllBooks() {
        List<BooksInfo> result = new ArrayList<>();
        for (BooksInfo book : books) {
            result.add(book);
        }
        return result;
    }

    @PostMapping("books/getBookInfoListByAuthor")
    @ApiOperation("Get books by Author")
    public List<BooksInfo> getBookInfoListByAuthor(@RequestBody Authors author) {
        if (books.stream().filter(booksInfo -> booksInfo.getAuthor().equals(author.getAuthorName())).collect(Collectors.toList()).isEmpty()) {
        throw new InvalidAuthorException(HttpStatus.NOT_FOUND);
        } else {
            return books.stream().filter(booksInfo -> booksInfo.getAuthor().equals(author.getAuthorName())).collect(Collectors.toList());
        }
    }

    @PostMapping("books/putBook")
    @ApiOperation("Put book in the Library")
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