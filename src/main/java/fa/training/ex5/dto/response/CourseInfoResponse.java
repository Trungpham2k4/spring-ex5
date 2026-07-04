package fa.training.ex5.dto.response;

import java.util.UUID;

public record CourseInfoResponse(UUID id, String courseName, Integer duration, String description) {
}
