package cc.maids.test.repositories;

import cc.maids.test.models.BorrowingRecord;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowingRecordsRepo extends JpaRepository<BorrowingRecord, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
        //    we have used the lock annotation to prevent patrons borrowing race on te same book
    Optional<BorrowingRecord> findByPatronIdAndBookIdAndRetrievingDateIsNull(Long patronId, Long bookId);
}
