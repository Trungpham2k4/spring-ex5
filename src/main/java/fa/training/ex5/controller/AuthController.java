package fa.training.ex5.controller;

import fa.training.ex5.dto.ResponseApi;
import fa.training.ex5.dto.request.AuthRequest;
import fa.training.ex5.dto.response.AuthResponse;
import fa.training.ex5.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseApi<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        String token = authService.authenticate(authRequest);
        return ResponseApi.<AuthResponse>builder()
                .status(HttpStatus.OK)
                .message("Login successful")
                .data(new AuthResponse(token))
                .build();
    }
}
