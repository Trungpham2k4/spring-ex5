package fa.training.ex5.service;

import com.github.f4b6a3.uuid.UuidCreator;
import fa.training.ex5.dto.request.UploadMaterialRequest;
import fa.training.ex5.dto.response.MaterialInfoResponse;
import fa.training.ex5.entity.Material;
import fa.training.ex5.enums.FileType;
import fa.training.ex5.exception.custom.FilenameException;
import fa.training.ex5.exception.custom.ResourceNotFoundException;
import fa.training.ex5.mapper.MaterialMapper;
import fa.training.ex5.utils.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialService {
    private final MaterialMapper materialMapper;

    public List<MaterialInfoResponse> getAllMaterials(UUID courseId) {
        return materialMapper.findAllByCourseId(courseId).stream()
                .map(material -> new MaterialInfoResponse(
                        material.getMaterialId(), material.getFileName(), material.getFileSize(),
                        material.getDescription(), material.getMaterialType().name(), material.getUploadDate()))
                .toList();
    }

    public MaterialInfoResponse getMaterialById(UUID materialId) {
        return materialMapper.findById(materialId)
                .map(material -> new MaterialInfoResponse(
                        material.getMaterialId(), material.getFileName(), material.getFileSize(),
                        material.getDescription(), material.getMaterialType().name(), material.getUploadDate()))
                .orElseThrow(() -> new ResourceNotFoundException("Material not found"));
    }

    @Transactional
    public void deleteMaterial(UUID materialId) {
        Material material = materialMapper.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found"));
        materialMapper.delete(materialId);
        log.info("Material deleted from DB: {}", materialId);

        try {
            Path path = Path.of(Constant.STORAGE, material.getStoreFileName()).toAbsolutePath();
            Files.deleteIfExists(path);
            log.info("Physical file deleted: {}", material.getStoreFileName());
        } catch (Exception e){
            log.error("Failed to delete physical file: {}", material.getStoreFileName());
            throw new RuntimeException("Failed to delete physical file: " + material.getStoreFileName(), e);
        }
    }

    @Transactional
    public void uploadMaterial(UUID courseId, UploadMaterialRequest request) {
        MultipartFile file = request.file();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (fileName.contains("..")) {
            throw new FilenameException("Cannot store file path with relative path outside current directory: " + fileName);
        }
        long fileSize = file.getSize();

        UUID materialId = UuidCreator.getTimeOrderedEpoch();
        String storeFileName = materialId.toString() + "_" + fileName;
        String fileType = file.getContentType();
        FileType contentType = FileType.fromMimeType(fileType);

        Material material = Material.builder()
                .materialId(materialId)
                .fileName(fileName)
                .storeFileName(storeFileName)
                .fileSize(fileSize)
                .fileType(contentType)
                .uploadDate(LocalDateTime.now())
                .description(request.description())
                .materialType(request.materialType())
                .build();
        materialMapper.save(courseId, material);
        try{
            Path storageDirectory = Path.of(Constant.STORAGE).toAbsolutePath();
            if(!Files.exists(storageDirectory)){
                Files.createDirectories(storageDirectory);
            }
            Path path = storageDirectory.resolve(material.getStoreFileName());
            file.transferTo(path);
        }catch (Exception e){
            log.error("Error save to physical storage: {}", e.getMessage());
            throw new RuntimeException("Error save to physical storage, rollback DB: " + e.getMessage());
        }
    }

    public Resource downloadMaterial(UUID materialId) {
        Material material = materialMapper.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found"));
        Path path = Path.of(Constant.STORAGE, material.getStoreFileName()).toAbsolutePath();
        try {
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File does not exist: " + material.getStoreFileName());
            }
        }catch (Exception e){
            log.error("Error downloading file: {}", e.getMessage());
            throw new ResourceNotFoundException("File not found: " + material.getStoreFileName());
        }
    }
}
