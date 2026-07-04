package fa.training.ex5.controller;

import fa.training.ex5.dto.ResponseApi;
import fa.training.ex5.dto.request.UserModifyRequest;
import fa.training.ex5.dto.response.UserInfoResponse;
import fa.training.ex5.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseApi<List<UserInfoResponse>> getAllUsers() {
        List<UserInfoResponse> users = userService.findAllUsers();
        return ResponseApi.<List<UserInfoResponse>>builder()
                .status(HttpStatus.OK)
                .data(users)
                .message("Successfully retrieved all users")
                .build();
    }

    @GetMapping("/{id}")
    public ResponseApi<UserInfoResponse> getUserById(@PathVariable UUID id) {
        UserInfoResponse user = userService.findUserById(id);
        return ResponseApi.<UserInfoResponse>builder()
                .data(user)
                .status(HttpStatus.OK)
                .message("Successfully retrieved user by id")
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseApi<String> createUser(@Valid @RequestBody UserModifyRequest user) {
        userService.insertUser(user);
        return ResponseApi.<String>builder()
                .status(HttpStatus.CREATED)
                .message("Successfully created user")
                .build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseApi<String> updateUser(@PathVariable UUID id, @Valid @RequestBody UserModifyRequest user) {
        userService.updateUser(id, user);
        return ResponseApi.<String>builder()
                .status(HttpStatus.OK)
                .message("Successfully updated user")
                .build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseApi<String> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseApi.<String>builder()
                .status(HttpStatus.NO_CONTENT)
                .message("Successfully deleted user")
                .build();
    }
}
