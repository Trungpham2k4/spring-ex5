package fa.training.ex5.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record MaterialInfoResponse(UUID materialId, String fileName, long fileSize,
                                   String description, String materialType, LocalDateTime uploadTime) {
}
