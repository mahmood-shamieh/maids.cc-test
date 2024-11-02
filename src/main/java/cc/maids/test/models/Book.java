package cc.maids.test.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.LastModifiedDate;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "books")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder

public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String author;
    @Column(nullable = false)
    private Integer publicationYear;
    @Builder.Default
    @Column(nullable = false)
    private Boolean available = true;
    @Column(length = 20, unique = true, nullable = false)
    private String isbn;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    List<BorrowingRecord> borrowingRecordList;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime lastDateModified;

}
