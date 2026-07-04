package fa.training.ex5.service;

import fa.training.ex5.dto.request.AuthRequest;
import fa.training.ex5.entity.Role;
import fa.training.ex5.entity.User;
import fa.training.ex5.exception.custom.UnauthenticatedException;
import fa.training.ex5.mapper.UserMapper;
import fa.training.ex5.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public String authenticate(AuthRequest authRequest) {
        // 1. Tìm user theo username
        User user = userMapper.findByUsername(authRequest.username());

        // 2. Validate user có tồn tại và password có khớp không
        if (user == null || !passwordEncoder.matches(authRequest.password(), user.getPassword())) {
            throw new UnauthenticatedException("Invalid username or password");
        }

        // 3. Nếu đúng, tạo JWT token
        return jwtTokenProvider.generateToken(user.getUserName(), user.getRoles().stream().map(Role::getRoleName).toList());
    }
}
