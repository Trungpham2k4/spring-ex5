package fa.training.ex5.mapper;

import fa.training.ex5.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface CourseMapper {
    Optional<Course> findById(@Param("courseId") UUID id);
    List<Course> findAll();
    List<Course> findPage(int offset, int limit);
    void save(Course course);
    void update(Course course);
    void delete(@Param("courseId") UUID id);

    List<Course> search(@Param("keyword") String keyword);
}
