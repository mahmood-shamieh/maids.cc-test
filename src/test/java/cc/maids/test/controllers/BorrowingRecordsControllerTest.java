package cc.maids.test.controllers;


import cc.maids.test.dto.BookDTO;
import cc.maids.test.dto.BorrowingRecordDTO;
import cc.maids.test.dto.PatronDTO;
import cc.maids.test.dto.ResponseDTO;
import cc.maids.test.exceptions.NotFoundException;
import cc.maids.test.models.Book;
import cc.maids.test.models.BorrowingRecord;
import cc.maids.test.models.Patron;
import cc.maids.test.services.BorrowingRecordsServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
public class BorrowingRecordsControllerTest {
    @InjectMocks
    private BorrowingRecordsController borrowingRecordsController;

    @Mock
    private BorrowingRecordsServices borrowingRecordsServices;

    private BorrowingRecord mockBorrowingRecord;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mock BorrowingRecord
        mockBorrowingRecord = BorrowingRecord.builder()
                .id(1L)
                .borrowingDate(LocalDateTime.now())
                .retrievingDate(LocalDateTime.now().plusDays(7))
                .book(Book.builder()  // Assuming you have a Book class with Lombok Builder
                        .id(1L)
                        .title("Test Book")
                        .author("Author Name")
                        .isbn("123456789")
                        .publicationYear(2020)
                        .createdAt(LocalDateTime.now())
                        .lastDateModified(LocalDateTime.now())
                        .build())
                .patron(Patron.builder() // Assuming you have a Patron class with Lombok Builder
                        .id(1L)
                        .name("Patron Name")
                        .email("patron@example.com")
                        .createdAt(LocalDateTime.now())
                        .lastDateModified(LocalDateTime.now())
                        .build())
                .createdAt(LocalDateTime.now())
                .lastDateModified(LocalDateTime.now())
                .build();
    }

    @Test
    public void testBorrowABook_Success() throws Exception {
        // Given
        when(borrowingRecordsServices.borrowABook(anyLong(), anyLong())).thenReturn(mockBorrowingRecord);

        // When
        ResponseEntity<?> response = borrowingRecordsController.borrowABook(1L, 1L);

        // Then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        ResponseDTO responseBody = (ResponseDTO) response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getData()).isNotNull();
//        assertThat(((Book)responseBody.getData()).getTitle()).isEqualTo("Test Book");
    }

    @Test
    public void testBorrowABook_NotFound() throws Exception {
        // Given
        when(borrowingRecordsServices.borrowABook(anyLong(), anyLong())).thenThrow(new NotFoundException("Book not found"));

        // When
        ResponseEntity<?> response = borrowingRecordsController.borrowABook(1L, 1L);

        // Then
        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        ResponseDTO responseBody = (ResponseDTO) response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getMessage()).isEqualTo("Book not found");
    }

    @Test
    public void testReturnOfABorrowedBook_Success() throws Exception {
        // Given
        when(borrowingRecordsServices.returnOfABorrowedBook(anyLong(), anyLong())).thenReturn(mockBorrowingRecord);

        // When
        ResponseEntity<?> response = borrowingRecordsController.returnOfABorrowedBook(1L, 1L);

        // Then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        ResponseDTO responseBody = (ResponseDTO) response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getData()).isNotNull();
//        assertThat(responseBody.getData().getBook().getTitle()).isEqualTo("Test Book");
    }

    @Test
    public void testReturnOfABorrowedBook_NotFound() throws Exception {
        // Given
        when(borrowingRecordsServices.returnOfABorrowedBook(anyLong(), anyLong())).thenThrow(new NotFoundException("Borrowing record not found"));

        // When
        ResponseEntity<?> response = borrowingRecordsController.returnOfABorrowedBook(1L, 1L);

        // Then
        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        ResponseDTO responseBody = (ResponseDTO) response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getMessage()).isEqualTo("Borrowing record not found");
    }
}
