package cc.maids.test.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "borrowing_records")

public class BorrowingRecord {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Patron patron;
    @ManyToOne
    private Book book;
    @Column(nullable = false)
    private LocalDateTime borrowingDate;
    @Column(nullable = true)
    private LocalDateTime retrievingDate;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime lastDateModified;


}
