package cc.maids.test.dto;


import cc.maids.test.validation.groups.BookValidation1;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public
//groups attributes here mention the parent group of the annotation role
class BookDTO {

    private Long id;

    @NotBlank(message = "Title is required",groups = BookValidation1.class)
    private String title;

    @NotBlank(message = "Author is required",groups = BookValidation1.class)
    private String author;

    @NotNull(message = "Publication year is required",
            groups = BookValidation1.class
    )
    private Integer publicationYear;

    @NotBlank(message = "ISBN is required",groups = BookValidation1.class)
    private String isbn;
    @NotNull(message = "Available is required",groups = BookValidation1.class)
    @Size(min = 1,max = 1,message = "Available should be a boolean value or just 0 or 1")
    private Boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime lastDateModified;

    // Getters and Setters
}