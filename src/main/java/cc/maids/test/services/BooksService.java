package cc.maids.test.services;

import cc.maids.test.dto.BookDTO;
import cc.maids.test.exceptions.FieldsValidationException;
import cc.maids.test.exceptions.NotFoundException;
import cc.maids.test.models.Book;
import cc.maids.test.repositories.BooksRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BooksService {
    final BooksRepo booksRepo;

    @Autowired
    public BooksService(BooksRepo booksRepo) {
        this.booksRepo = booksRepo;
    }

    @Cacheable(value = "books")
    public List<Book> getAllBooks() throws Exception {

        return booksRepo.findAll();

    }

    @Cacheable(value = "book", key = "#id")
    public Optional<Book> getBookById(Long id) throws Exception {

        return booksRepo.findById(id);

    }


    @CacheEvict(value = "books", allEntries = true)
    public Book createBook(BookDTO bookDto) throws Exception {
        boolean checkISBN = booksRepo.existsByIsbn(bookDto.getIsbn());
        if (checkISBN) {
            throw new FieldsValidationException("ISBN is used before (Duplicate Entry)", Map.ofEntries(Map.entry("isbn", "ISBN is used before")));
        }
        Book book = cc.maids.test.models.Book.builder()
                .title(bookDto.getTitle())
                .author(bookDto.getAuthor())
                .available(bookDto.getAvailable())
                .isbn(bookDto.getIsbn())
                .publicationYear(bookDto.getPublicationYear())
                .build();
        return booksRepo.save(book);


    }

    @CachePut(value = "book", key = "#id")
    @CacheEvict(value = "books", allEntries = true)
    public Book updateBook(BookDTO bookDTO, Long id) throws Exception {
        Optional<Book> foundedBook = booksRepo.findById(id);
        if (foundedBook.isPresent()) {
            Optional<Book> isbnUser = booksRepo.findByIsbn(bookDTO.getIsbn());

            if (isbnUser.isPresent() && isbnUser.get().getId() != id)
                throw new FieldsValidationException("ISBN is used before (Duplicate Entry)",
                        Map.ofEntries(Map.entry("isbn", "ISBN is used before")));

            Book savedBook = booksRepo.save(Book.builder()
                    .isbn(bookDTO.getIsbn())
                    .title(bookDTO.getTitle())
                    .available(bookDTO.getAvailable())
                    .publicationYear(bookDTO.getPublicationYear())
                    .createdAt(foundedBook.get().getCreatedAt())
                    .author(bookDTO.getAuthor())
                    .id(id)
                    .build());
            return savedBook;
        } else {
            throw new NotFoundException("Book not found");
        }
    }

    @Caching(evict = {
            @CacheEvict(value = "books", allEntries = true),
            @CacheEvict(value = "book", key = "#id")
    })
    public void deleteBook(Long id) throws Exception {
        if (booksRepo.existsById(id)) {
            booksRepo.deleteById(id);
        } else {
            throw new NotFoundException("Book not found");
        }
    }
}
