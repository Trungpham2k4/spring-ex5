package fa.training.ex5.mapper;

import fa.training.ex5.entity.Material;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface MaterialMapper {
    List<Material> findAllByCourseId(@Param("courseId") UUID courseId);
    Optional<Material> findById(UUID id);
    void save(@Param("courseId") UUID courseId,@Param("material") Material material);
    void delete(@Param("materialId") UUID id);
}
