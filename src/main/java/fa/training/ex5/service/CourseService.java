package fa.training.ex5.service;

import com.github.f4b6a3.uuid.UuidCreator;
import fa.training.ex5.dto.request.CourseModifyRequest;
import fa.training.ex5.dto.response.CourseInfoResponse;
import fa.training.ex5.entity.Course;
import fa.training.ex5.exception.custom.ResourceNotFoundException;
import fa.training.ex5.mapper.CourseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseMapper courseMapper;

    public List<CourseInfoResponse> getAllCourses(int page, int size) {
        page = page < 1 ? 1 : (page - 1);
        size = size < 1 ? 10 : size;
        int offset = page * size;
        return courseMapper.findPage(offset, size).stream()
                .map(course -> new CourseInfoResponse(course.getCourseId(), course.getCourseName(), course.getDuration(), course.getDescription()))
                .toList();
    }

    public CourseInfoResponse getCourseById(UUID courseId) {
        Course course = courseMapper.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        return new CourseInfoResponse(course.getCourseId(), course.getCourseName(), course.getDuration(), course.getDescription());
    }

    @Transactional
    public void addCourse(CourseModifyRequest courseModifyRequest) {
        Course course = Course.builder()
                .courseId(UuidCreator.getTimeOrderedEpoch())
                .courseName(courseModifyRequest.courseName())
                .duration(courseModifyRequest.duration())
                .description(courseModifyRequest.description())
                .build();
        courseMapper.save(course);
        log.info("Course added: {}", course);
    }

    @Transactional
    public void updateCourse(UUID courseId, CourseModifyRequest courseModifyRequest) {
        Course course = courseMapper.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        course.setCourseName(courseModifyRequest.courseName());
        course.setDuration(courseModifyRequest.duration());
        course.setDescription(courseModifyRequest.description());
        courseMapper.update(course);
        log.info("Course updated: {}", course);
    }

    @Transactional
    public void deleteCourse(UUID courseId) {
        Course course = courseMapper.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        courseMapper.delete(courseId);
        log.info("Course deleted: {}", course);
    }

    public List<CourseInfoResponse> search(String keyword) {
        return courseMapper.search(keyword).stream()
                .map(course -> new CourseInfoResponse(course.getCourseId(), course.getCourseName(), course.getDuration(), course.getDescription()))
                .toList();
    }
}
