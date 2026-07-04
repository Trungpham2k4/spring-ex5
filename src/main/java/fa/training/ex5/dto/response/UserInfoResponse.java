package fa.training.ex5.dto.response;

import java.util.UUID;

public record UserInfoResponse(UUID id, String username, String fullName) {
    @Override
    public String toString() {
        return "UserInfoResponse{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
