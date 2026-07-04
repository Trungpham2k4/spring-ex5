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
public class User {
    UUID userId;
    String userName;
    String password;
    String fullName;
    String email;
    String status;

    List<Role> roles = new ArrayList<>();
}
