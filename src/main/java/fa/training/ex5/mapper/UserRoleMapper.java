package fa.training.ex5.mapper;

import fa.training.ex5.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.UUID;

@Mapper
public interface UserRoleMapper {
    void insertUserRole(UserRole userRole);
    void deleteUserRoleByUserId(UUID userId);
}
