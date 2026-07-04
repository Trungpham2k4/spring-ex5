package fa.training.ex5.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record UserModifyRequest(
        @NotBlank(message = "Username is required")
        String username,
        String password,
        @Email(message = "Email should be valid")
        String email,
        String fullName,

        @NotBlank
        @Pattern(regexp = "ACTIVE|INACTIVE", message = "Status must be either ACTIVE or INACTIVE")
        String status,

        List<String> roles
) {
}
