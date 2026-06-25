package ru.nelakov.libraryapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthorRequest(
        @NotBlank
        @Size(max = 255)
        @Pattern(regexp = "[^/\\\\]+", message = "must not contain a slash")
        String name
) {
}
