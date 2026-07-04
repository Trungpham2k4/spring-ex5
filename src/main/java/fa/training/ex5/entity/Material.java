package fa.training.ex5.entity;

import fa.training.ex5.enums.FileType;
import fa.training.ex5.enums.MaterialType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Material {
    UUID materialId;
    String fileName;
    String storeFileName;
    long fileSize;
    FileType fileType;
    LocalDateTime uploadDate;
    String description;
    MaterialType materialType;
}
