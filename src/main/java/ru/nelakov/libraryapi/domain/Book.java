package ru.nelakov.libraryapi.domain;

import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Book(String title, String author, String bookName, Integer pages, LocalDate publishDate) {
}
