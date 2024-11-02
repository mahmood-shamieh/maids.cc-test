package cc.maids.test.controllers;

import cc.maids.test.dto.BookDTO;
import cc.maids.test.exceptions.NotFoundException;
import cc.maids.test.models.Book;
import cc.maids.test.services.BooksService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BooksControllerTest {
    @InjectMocks
    private BooksController booksController;

    @Mock
    private BooksService booksService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(booksController).build();
    }

    @Test
    void getAllBooks_ReturnsListOfBooks() throws Exception {
        Book book1 = Book.builder()
                .title("Book 1")
                .isbn("ISBN1")
                .available(true)
                .author("Author 1")
                .publicationYear(2020)
                .build();
        Book book2 = Book.builder()
                .title("Book 2")
                .isbn("ISBN2")
                .available(true)
                .author("Author 2")
                .publicationYear(2022)
                .build();

        when(booksService.getAllBooks()).thenReturn(Arrays.asList(book1, book2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data[0].title").value("Book 1"))
                .andExpect(jsonPath("$.data[1].title").value("Book 2"));

        verify(booksService, times(1)).getAllBooks();
    }

    @Test
    void getBookById_ReturnsBook_WhenBookExists() throws Exception {
        Book book = Book.builder()
                .title("Book 1")
                .isbn("ISBN1")
                .available(true)
                .author("Author 1")
                .publicationYear(2020)
                .build();


        when(booksService.getBookById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Book 1"));

        verify(booksService, times(1)).getBookById(1L);
    }

    @Test
    void getBookById_ReturnsNoContent_WhenBookDoesNotExist() throws Exception {
        when(booksService.getBookById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(booksService, times(1)).getBookById(1L);
    }

    @Test
    void createBook_ReturnsCreatedBook() throws Exception {
        BookDTO bookDTO = BookDTO.builder()
                .title("Book 1")
                .isbn("ISBN1")
                .available(true)
                .author("Author 1")
                .publicationYear(2020)
                .build();

        Book book = Book.builder()
                .title("Book 1")
                .isbn("ISBN1")
                .available(true)
                .author("Author 1")
                .publicationYear(2020)
                .build();


        when(booksService.createBook(any(BookDTO.class))).thenReturn(book);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Book 1"));

        verify(booksService, times(1)).createBook(any(BookDTO.class));
    }

    @Test
    void createBook_ReturnsValidationError() throws Exception {
        BookDTO invalidBookDTO = BookDTO.builder()
                .title("")
                .isbn("ISBN1")
                .available(true)
                .author("Author 1")
                .publicationYear(2020)
                .build();


        mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBookDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Fields validation errors"));
    }

    @Test
    void updateBook_ReturnsUpdatedBook() throws Exception {
        BookDTO bookDTO = BookDTO.builder()
                .title("Updated Book")
                .isbn("ISBN1")
                .available(true)
                .author("Author 1")
                .publicationYear(2020)
                .build();

        Book updatedBook = Book.builder()
                .id(1L)
                .title("Updated Book")
                .isbn("ISBN1")
                .available(true)
                .author("Author 1")
                .publicationYear(2020)
                .build();
        when(booksService.updateBook(any(BookDTO.class), eq(1L))).thenReturn(updatedBook);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/books/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Updated Book"));

        verify(booksService, times(1)).updateBook(any(BookDTO.class), eq(1L));
    }

    @Test
    void updateBook_ReturnsNoContent_WhenNotFound() throws Exception {
        BookDTO bookDTO = BookDTO.builder()
                .title("Updated Book")
                .isbn("ISBN1")
                .available(true)
                .author("Author 1")
                .publicationYear(2020)
                .build();


        when(booksService.updateBook(any(BookDTO.class), eq(1L))).thenThrow(new NotFoundException("Book not found"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/books/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDTO)))
                .andExpect(status().isNoContent());

        verify(booksService, times(1)).updateBook(any(BookDTO.class), eq(1L));
    }

    @Test
    void deleteBook_ReturnsSuccessMessage() throws Exception {
        doNothing().when(booksService).deleteBook(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Element deleted successfully"));

        verify(booksService, times(1)).deleteBook(1L);
    }

    @Test
    void deleteBook_ReturnsNoContent_WhenNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(booksService).deleteBook(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/{id}", 1))
                .andExpect(status().isNoContent());

        verify(booksService, times(1)).deleteBook(1L);
    }

}
