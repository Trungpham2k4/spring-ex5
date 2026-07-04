package fa.training.ex5.utils.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER}) // Áp dụng cho thuộc tính trong DTO hoặc tham số Controller
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileValidator.class) // Chỉ định class sẽ xử lý logic validate
public @interface ValidFile {
    String message() default "Invalid File"; // Tin nhắn mặc định
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}