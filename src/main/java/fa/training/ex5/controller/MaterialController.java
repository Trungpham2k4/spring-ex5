package fa.training.ex5.controller;

import fa.training.ex5.dto.ResponseApi;
import fa.training.ex5.dto.response.MaterialInfoResponse;
import fa.training.ex5.service.MaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @DeleteMapping("/{materialId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseApi<String> deleteMaterial(@PathVariable UUID materialId){
        materialService.deleteMaterial(materialId);
        return ResponseApi.<String>builder()
                .status(HttpStatus.NO_CONTENT)
                .message("Material deleted successfully")
                .build();
    }

    @GetMapping("/{materialId}/download")
    public ResponseEntity<Resource> downloadMaterial(@PathVariable UUID materialId){
        MaterialInfoResponse material = materialService.getMaterialById(materialId);
        Resource resource = materialService.downloadMaterial(materialId);
        try{
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + material.fileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("Error downloading material: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
