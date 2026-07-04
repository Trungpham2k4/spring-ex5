package fa.training.ex5.utils.annotations;

import fa.training.ex5.enums.FileType;
import fa.training.ex5.utils.Constant;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        // Nếu không có file, hãy để @NotNull xử lý (nếu cần bắt buộc)
        if (file == null) {
            return true; // Để @NotNull bắt
        }
        if (file.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File cannot be empty (0 bytes)")
                    .addConstraintViolation();
            return false;
        }

        // 1. Validate dung lượng file (Yêu cầu Ex4: Max 10MB)
        if (file.getSize() > Constant.MAX_SIZE) {
            context.disableDefaultConstraintViolation(); // Tắt tin nhắn mặc định
            context.buildConstraintViolationWithTemplate("File size exceeds maximum allowed (10MB)")
                    .addConstraintViolation();
            return false;
        }

        // 2. Validate loại file qua MIME Type (Yêu cầu Ex4: Chỉ cho phép pdf, docx, pptx, zip, txt)
        String contentType = file.getContentType();
        if (!FileType.isSupported(contentType)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File content type not supported. Only accept: PDF, DOCX, PPTX, ZIP, TXT.")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}