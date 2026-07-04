package fa.training.ex5.controller;

import fa.training.ex5.dto.ResponseApi;
import fa.training.ex5.dto.request.CourseModifyRequest;
import fa.training.ex5.dto.request.UploadMaterialRequest;
import fa.training.ex5.dto.response.CourseInfoResponse;
import fa.training.ex5.dto.response.MaterialInfoResponse;
import fa.training.ex5.service.CourseService;
import fa.training.ex5.service.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final MaterialService materialService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseApi<List<CourseInfoResponse>> getAllCourses(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        List<CourseInfoResponse> courses = courseService.getAllCourses(page, size);
        return ResponseApi.<List<CourseInfoResponse>>builder()
                .data(courses)
                .status(HttpStatus.OK)
                .message("Get all courses successfully")
                .build();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseApi<CourseInfoResponse> getCourseById(@PathVariable UUID id) {
        CourseInfoResponse course = courseService.getCourseById(id);
        return ResponseApi.<CourseInfoResponse>builder()
                .data(course)
                .status(HttpStatus.OK)
                .message("Get course successfully")
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseApi<String> createCourse(@Valid @RequestBody CourseModifyRequest request){
        courseService.addCourse(request);
        return ResponseApi.<String>builder()
                .status(HttpStatus.CREATED)
                .message("Create course successfully")
                .build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseApi<String> updateCourse(@PathVariable UUID id, @Valid @RequestBody CourseModifyRequest request){
        courseService.updateCourse(id, request);
        return ResponseApi.<String>builder()
                .status(HttpStatus.OK)
                .message("Update course successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseApi<String> deleteCourse(@PathVariable UUID id){
        courseService.deleteCourse(id);
        return ResponseApi.<String>builder()
                .status(HttpStatus.NO_CONTENT)
                .message("Delete course successfully")
                .build();
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseApi<List<CourseInfoResponse>> searchCourses(@RequestParam(name = "keyword", defaultValue = "") String keyword) {
        List<CourseInfoResponse> courses = courseService.search(keyword);
        return ResponseApi.<List<CourseInfoResponse>>builder()
                .data(courses)
                .status(HttpStatus.OK)
                .message("Search courses successfully")
                .build();
    }

    @GetMapping("/{courseId}/materials")
    @ResponseStatus(HttpStatus.OK)
    public ResponseApi<List<MaterialInfoResponse>> getAllMaterials(@PathVariable UUID courseId){
        List<MaterialInfoResponse> materials = materialService.getAllMaterials(courseId);
        return ResponseApi.<List<MaterialInfoResponse>>builder()
                .data(materials)
                .status(HttpStatus.OK)
                .message("Get all materials successfully")
                .build();
    }

    @PostMapping(value = "/{courseId}/materials", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseApi<String> uploadMaterial(
            @PathVariable UUID courseId,
            @Valid @ModelAttribute UploadMaterialRequest uploadMaterialRequest
    ) {
        materialService.uploadMaterial(courseId, uploadMaterialRequest);
        return ResponseApi.<String>builder()
                .status(HttpStatus.CREATED)
                .message("Upload material successfully")
                .build();
    }
}
