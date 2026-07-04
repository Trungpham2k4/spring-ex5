package fa.training.ex5.dto.request;

import fa.training.ex5.enums.MaterialType;
import fa.training.ex5.utils.annotations.ValidFile;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UploadMaterialRequest(
        @NotNull
        @ValidFile
        MultipartFile file,
        String description,
        @NotNull
        MaterialType materialType) {
}
