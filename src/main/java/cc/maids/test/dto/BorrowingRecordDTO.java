package cc.maids.test.dto;

import cc.maids.test.models.Book;
import cc.maids.test.models.Patron;
import lombok.*;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class BorrowingRecordDTO {
    private Long id;
    private PatronDTO patron;
    private BookDTO book;
    private LocalDateTime borrowingDate;
    private LocalDateTime retrievingDate;
    private LocalDateTime createdAt;
    private LocalDateTime lastDateModified;
}
