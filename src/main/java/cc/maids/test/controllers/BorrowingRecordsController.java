package cc.maids.test.controllers;

import cc.maids.test.dto.BookDTO;
import cc.maids.test.dto.BorrowingRecordDTO;
import cc.maids.test.dto.PatronDTO;
import cc.maids.test.dto.ResponseDTO;
import cc.maids.test.exceptions.NotFoundException;
import cc.maids.test.models.BorrowingRecord;
import cc.maids.test.services.BorrowingRecordsServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BorrowingRecordsController {
    @Autowired
    BorrowingRecordsServices borrowingRecordsServices;

    @PostMapping("/borrow/{bookId}/patron/{patronId}")
    public ResponseEntity borrowABook(@PathVariable Long bookId, @PathVariable Long patronId) {
        try {
            BorrowingRecord borrowingRecord = borrowingRecordsServices.borrowABook(bookId, patronId);
            return ResponseEntity.ok()
                    .body(ResponseDTO.successResponse(BorrowingRecordDTO.builder()
                            .book(BookDTO.builder()
                                    .title(borrowingRecord.getBook().getTitle())
                                    .isbn(borrowingRecord.getBook().getIsbn())
                                    .available(true)
//                                    we have assign the value of book availability to true manually because this state present the state
//                                    before the borrowing action
                                    .author(borrowingRecord.getBook().getAuthor())
                                    .publicationYear(borrowingRecord.getBook().getPublicationYear())
                                    .createdAt(borrowingRecord.getBook().getCreatedAt())
                                    .lastDateModified(borrowingRecord.getBook().getLastDateModified())
                                    .id(borrowingRecord.getBook().getId())
                                    .build())
                            .patron(PatronDTO.builder()
                                    .email(borrowingRecord.getPatron().getEmail())
                                    .id(borrowingRecord.getPatron().getId())
                                    .name(borrowingRecord.getPatron().getName())
                                    .createdAt(borrowingRecord.getPatron().getCreatedAt())
                                    .lastDateModified(borrowingRecord.getPatron().getLastDateModified())
                                    .build())
                            .borrowingDate(borrowingRecord.getBorrowingDate())
                            .retrievingDate(borrowingRecord.getRetrievingDate())
                            .createdAt(borrowingRecord.getCreatedAt())
                            .lastDateModified(borrowingRecord.getLastDateModified())
                            .id(borrowingRecord.getId())
                            .build()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(500).body(ResponseDTO.badResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ResponseDTO.badResponse(e.getMessage()));
        }
    }

    @PutMapping("/return/{bookId}/patron/{patronId}")
    public ResponseEntity returnOfABorrowedBook(@PathVariable Long bookId, @PathVariable Long patronId) {
        try {
            BorrowingRecord borrowingRecord = borrowingRecordsServices.returnOfABorrowedBook(bookId, patronId);
            return ResponseEntity.ok()
                    .body(ResponseDTO.successResponse(BorrowingRecordDTO.builder()
                            .book(BookDTO.builder()
                                    .title(borrowingRecord.getBook().getTitle())
                                    .isbn(borrowingRecord.getBook().getIsbn())
                                    .publicationYear(borrowingRecord.getBook().getPublicationYear())
                                    .available(false)
//                                    we have assign the value of book availability to false manually because this state present the state
//                                    before the retrieving action
                                    .author(borrowingRecord.getBook().getAuthor())
                                    .createdAt(borrowingRecord.getBook().getCreatedAt())
                                    .lastDateModified(borrowingRecord.getBook().getLastDateModified())
                                    .id(borrowingRecord.getBook().getId())
                                    .build())
                            .patron(PatronDTO.builder()
                                    .email(borrowingRecord.getPatron().getEmail())
                                    .id(borrowingRecord.getPatron().getId())
                                    .name(borrowingRecord.getPatron().getName())
                                    .createdAt(borrowingRecord.getPatron().getCreatedAt())
                                    .lastDateModified(borrowingRecord.getPatron().getLastDateModified())
                                    .build())
                            .borrowingDate(borrowingRecord.getBorrowingDate())
                            .retrievingDate(borrowingRecord.getRetrievingDate())
                            .createdAt(borrowingRecord.getCreatedAt())
                            .lastDateModified(borrowingRecord.getLastDateModified())
                            .id(borrowingRecord.getId())
                            .build()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(500).body(ResponseDTO.badResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ResponseDTO.badResponse(e.getMessage()));
        }
    }
}
