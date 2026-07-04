package fa.training.ex5.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Course {
    UUID courseId;
    String courseName;
    Integer duration;
    String description;

    List<Material> materials = new ArrayList<>();
}
