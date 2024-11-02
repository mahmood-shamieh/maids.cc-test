package cc.maids.test.services;

import cc.maids.test.dto.BookDTO;
import cc.maids.test.exceptions.NotFoundException;
import cc.maids.test.models.Book;
import cc.maids.test.models.BorrowingRecord;
import cc.maids.test.models.Patron;
import cc.maids.test.repositories.BorrowingRecordsRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BorrowingRecordsServices {

    final BorrowingRecordsRepo borrowingRecordsRepo;
    final BooksService booksService;
    final PatronsService patronsService;

    @Autowired
    public BorrowingRecordsServices(BorrowingRecordsRepo borrowingRecordsRepo, BooksService booksService, PatronsService patronsService) {
        this.borrowingRecordsRepo = borrowingRecordsRepo;
        this.booksService = booksService;
        this.patronsService = patronsService;
    }

    @Transactional
    public BorrowingRecord borrowABook(Long bookId, Long patronId) throws Exception {
        Optional<BorrowingRecord> checkBorrowing = borrowingRecordsRepo.findByPatronIdAndBookIdAndRetrievingDateIsNull(patronId, bookId);

        Optional<Book> book = booksService.getBookById(bookId);
        if (book.isEmpty())
            throw new NotFoundException("There is no book related to this ID");
        else if (!book.get().getAvailable())
            throw new Exception("The book is unavailable now sorry :).");
        if (checkBorrowing.isPresent())
            throw new Exception("The patron is already borrowed this book");
        Optional<Patron> patron = patronsService.getPatronById(patronId);
        if (patron.isEmpty())
            throw new NotFoundException("There is no patron related to this ID");
        Book bookResult = book.get();
        bookResult.setAvailable(false);
        Patron patronResult = patron.get();

        return borrowingRecordsRepo.save(BorrowingRecord.builder()
                .book(bookResult)
                .patron(patronResult)
                .borrowingDate(LocalDateTime.now())
                .build());
    }

    @Transactional
    public BorrowingRecord returnOfABorrowedBook(Long bookId, Long patronId) throws Exception {
        Optional<BorrowingRecord> checkBorrowing = borrowingRecordsRepo.findByPatronIdAndBookIdAndRetrievingDateIsNull(patronId, bookId);
        if (checkBorrowing.isEmpty())
            throw new NotFoundException("No borrowing record related to this patron at this book");
        BorrowingRecord borrowingResult = checkBorrowing.get();
        if (borrowingResult.getRetrievingDate() != null)
            throw new Exception("The patron is already Returned this book");
        BorrowingRecord updatedBorrowingRecord = borrowingRecordsRepo.save(BorrowingRecord.builder()
                .book(borrowingResult.getBook())
                .patron(borrowingResult.getPatron())
                .borrowingDate(borrowingResult.getBorrowingDate())
                .id(borrowingResult.getId())
                .createdAt(borrowingResult.getCreatedAt())
                .retrievingDate(LocalDateTime.now())
                .build());
        updatedBorrowingRecord.getBook().setAvailable(true);
        if(updatedBorrowingRecord.getBook() == null)
            throw new NotFoundException("There is so related book for this ID");


        return updatedBorrowingRecord;
    }
}
