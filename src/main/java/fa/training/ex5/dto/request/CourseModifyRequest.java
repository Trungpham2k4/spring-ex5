package fa.training.ex5.dto.request;

import jakarta.validation.constraints.*;

import java.util.List;

public record CourseModifyRequest(
        @NotBlank(message = "Course name is required")
        String courseName,
        @NotNull(message = "Duration is required")
        @Min(value = 1, message = "Duration must be greater than 0")
        Integer duration,
        @Size(max = 500, message = "Description must not exceed 500 characters")
        @NotBlank
        String description
) {
}
