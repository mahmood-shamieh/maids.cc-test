package cc.maids.test.controllers;

import cc.maids.test.dto.*;
import cc.maids.test.dto.ResponseDTO;
import cc.maids.test.exceptions.FieldsValidationException;
import cc.maids.test.exceptions.NotFoundException;
import cc.maids.test.models.Book;
import cc.maids.test.services.BooksService;
import cc.maids.test.validation.groups.BookValidation1;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BooksController {
    @Autowired
    BooksService booksService;

    @GetMapping
    public ResponseEntity getAllBooks() {
        try {
            return ResponseEntity.ok(ResponseDTO.successResponse(booksService.getAllBooks().stream().map(e -> BookDTO.builder()
                    .id(e.getId())
                    .title(e.getTitle())
                    .isbn(e.getIsbn())
                    .available(e.getAvailable())
                    .author(e.getAuthor())
                    .publicationYear(e.getPublicationYear())
                    .createdAt(e.getCreatedAt())
                    .lastDateModified(e.getLastDateModified()).build()).collect(Collectors.toList())));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ResponseDTO.badResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getBookById(@PathVariable Long id) {
        try {
            Optional<Book> book = booksService.getBookById(id);
            if (book.isPresent()) {
                Book foundedBook = book.get();
                return ResponseEntity.ok(BookDTO.builder()
                        .id(foundedBook.getId())
                        .publicationYear(foundedBook.getPublicationYear())
                        .author(foundedBook.getAuthor())
                        .available(foundedBook.getAvailable())
                        .lastDateModified(foundedBook.getLastDateModified())
                        .createdAt(foundedBook.getCreatedAt())
                        .isbn(foundedBook.getIsbn())
                        .title(foundedBook.getTitle())
                        .build());
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ResponseDTO.badResponse("unknown error happened"));
        }
    }

    @PostMapping()
    public ResponseEntity createBook(@RequestBody BookDTO bookDto) {
        try {
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            Set<ConstraintViolation<BookDTO>> violations = validator.validate(bookDto, BookValidation1.class);
            if (violations != null && !violations.isEmpty()) {
                Map errors = violations.stream()
                        .map(violation ->
                                Map.entry(violation.getPropertyPath(), violation.getMessage())
                        ).collect(Collectors.toMap(
                                        element -> element.getKey(),
                                        element1 -> element1.getValue()
                                )
                        );
                throw new FieldsValidationException("Fields validation errors", errors);
            }
            Book book = booksService.createBook(bookDto);
            return ResponseEntity.ok(BookDTO.builder()
                    .title(book.getTitle())
                    .isbn(book.getIsbn())
                    .author(book.getAuthor())
                    .id(book.getId())
                    .available(book.getAvailable())
                    .publicationYear(book.getPublicationYear())
                    .createdAt(book.getCreatedAt())
                    .lastDateModified(book.getLastDateModified())
                    .build());
        } catch (FieldsValidationException e) {
            return ResponseEntity.status(500).body(ResponseDTO.badResponse(e.getMessage(), e.getErrors()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ResponseDTO.badResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateBook(@RequestBody BookDTO bookDto, @PathVariable Long id) {
        try {
            Book book = booksService.updateBook(bookDto, id);
            return ResponseEntity.ok().body(ResponseDTO.successResponse(BookDTO.builder()
                    .publicationYear(book.getPublicationYear())
                    .title(book.getTitle())
                    .isbn(book.getIsbn())
                    .id(book.getId())
                    .available(book.getAvailable())
                    .createdAt(book.getCreatedAt())
                    .lastDateModified(book.getLastDateModified())
                    .author(book.getAuthor())
                    .build()));
        } catch (FieldsValidationException e) {
            return ResponseEntity.status(500).body(ResponseDTO.badResponse("Fields validation errors", e.getErrors()));
        } catch (NotFoundException e) {
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.noContent().build();
        } catch (Exception e) {


            return ResponseEntity.status(500).body(ResponseDTO.badResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteBook(@PathVariable Long id) {
        try {
            booksService.deleteBook(id);
            return ResponseEntity.ok().body(ResponseDTO.successResponse("Element deleted successfully"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }


    }

}
