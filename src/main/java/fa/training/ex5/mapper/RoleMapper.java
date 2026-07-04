package fa.training.ex5.mapper;

import fa.training.ex5.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RoleMapper {
    List<Role> findAll();
    Optional<Role> findByRoleName(String roleName);
}
