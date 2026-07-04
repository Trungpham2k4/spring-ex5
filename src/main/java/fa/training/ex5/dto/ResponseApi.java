package fa.training.ex5.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Setter
@Getter
public class ResponseApi<T>{
    T data;
    HttpStatus status;
    String message;
    List<String> errors;
}
