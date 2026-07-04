package fa.training.ex5.dto.request;

import lombok.ToString;

public record AuthRequest(
        String username,
        @ToString.Exclude
        String password) {
}
