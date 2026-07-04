package fa.training.ex5.service;

import com.github.f4b6a3.uuid.UuidCreator;
import fa.training.ex5.dto.request.UserModifyRequest;
import fa.training.ex5.dto.response.UserInfoResponse;
import fa.training.ex5.entity.Role;
import fa.training.ex5.entity.User;
import fa.training.ex5.entity.UserRole;
import fa.training.ex5.exception.custom.ResourceNotFoundException;
import fa.training.ex5.mapper.RoleMapper;
import fa.training.ex5.mapper.UserMapper;
import fa.training.ex5.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("@userMapper.findById(#id).userName == authentication.name")
    public UserInfoResponse findUserById(UUID id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        UserInfoResponse userInfoResponse = new UserInfoResponse(id, user.getUserName(), user.getFullName());
        log.info("User found: {}", userInfoResponse);
        return userInfoResponse;
    }

    public List<UserInfoResponse> findAllUsers(){
        List<User> users = userMapper.findAll();
        List<UserInfoResponse> userInfoResponses = users.stream()
                .map(user -> new UserInfoResponse(user.getUserId(), user.getUserName(), user.getFullName()))
                .toList();
        log.info("Users found: {}", userInfoResponses);
        return userInfoResponses;
    }

    @Transactional
    public void insertUser(UserModifyRequest request) {
        User isExist = userMapper.findUserExist(request.username(), request.email());
        if (isExist != null) {
            throw new DuplicateKeyException("User already exists");
        }
        User user = User.builder()
                .userId(UuidCreator.getTimeOrderedEpoch())
                .userName(request.username())
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .status("ACTIVE")
                .build();
        userMapper.insert(user);
        log.info("User created: {}", user.getUserId());

        request.roles().forEach(role -> {
            String roleNameUpper = role.toUpperCase();
            Role storeRole = roleMapper.findByRoleName(roleNameUpper)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + role));
            UserRole userRole = UserRole.builder()
                    .userId(user.getUserId())
                    .roleId(storeRole.getRoleId())
                    .build();
            userRoleMapper.insertUserRole(userRole);
            log.info("Role {} assigned to user {}", role, user.getUserId());
        });
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userMapper.delete(user.getUserId());
        log.info("User deleted: {}", user.getUserId());

        userRoleMapper.deleteUserRoleByUserId(id);
        log.info("Roles deleted for user: {}", user.getUserId());
    }

    @Transactional
    public void updateUser(UUID id, UserModifyRequest request) {
        User user = userMapper.findUserWithRoles(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        user.setUserName(request.username() != null ? request.username() : user.getUserName());
        user.setFullName(request.fullName() != null ? request.fullName() : user.getFullName());
        user.setEmail(request.email() != null ? request.email() : user.getEmail());
        if (request.password() != null && !request.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        user.setStatus(request.status());
        userMapper.update(user);
        log.info("User updated: {}", user.getUserId());

        if(request.roles() != null && !request.roles().isEmpty()) {
            userRoleMapper.deleteUserRoleByUserId(id);

            request.roles().forEach(role -> {
                String roleNameUpper = role.toUpperCase();
                Role storeRole = roleMapper.findByRoleName(roleNameUpper)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + role));
                UserRole userRole = UserRole.builder()
                        .userId(user.getUserId())
                        .roleId(storeRole.getRoleId())
                        .build();
                userRoleMapper.insertUserRole(userRole);
            });
        }
    }
}
