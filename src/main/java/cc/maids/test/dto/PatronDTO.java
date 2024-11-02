package cc.maids.test.dto;

import cc.maids.test.validation.groups.BookValidation1;
import cc.maids.test.validation.groups.PatronsValidation1;
import jakarta.validation.constraints.Email;
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
//groups attributes here mention the parent group of the annotation role
public class PatronDTO {


    private Long id;

    @NotBlank(message = "Name is required", groups = PatronsValidation1.class)
    private String name;
    @NotBlank(message = "Email is required", groups = PatronsValidation1.class)
    @Email(message = "Email isn't valid", groups = PatronsValidation1.class)
    private String email;
    @NotBlank(message = "Password is required", groups = PatronsValidation1.class)
    @Size(min = 8 ,message = "password should be more thn 8 characters",groups = PatronsValidation1.class)
    private String password;

    private LocalDateTime createdAt;
    private LocalDateTime lastDateModified;

}
