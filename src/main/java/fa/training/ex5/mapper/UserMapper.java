package fa.training.ex5.mapper;

import fa.training.ex5.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface UserMapper {
    User findById(@Param("userId") UUID id);
    List<User> findAll();
    void insert(User user);
    void update(User user);
    void delete(@Param("userId") UUID id);

    User findByUsername(@Param("username") String username);
    User findUserWithRoles(@Param("userId") UUID id);

    User findUserExist(@Param("username") String username, @Param("email") String email);
}
