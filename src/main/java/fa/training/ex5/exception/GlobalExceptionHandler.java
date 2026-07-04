package fa.training.ex5.exception;

import fa.training.ex5.dto.ResponseApi;
import fa.training.ex5.exception.custom.ResourceNotFoundException;
import fa.training.ex5.exception.custom.UnauthenticatedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseApi<Void> handleNotFoundException(ResourceNotFoundException e) {
        return ResponseApi.<Void>builder()
                .status(HttpStatus.NOT_FOUND)
                .errors(List.of(e.getMessage()))
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseApi<String> handleValidation(
            MethodArgumentNotValidException e) {

        return ResponseApi.<String>builder()
                .status(HttpStatus.BAD_REQUEST)
                .errors(
                        e.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error ->
                                        error.getField() + ": " + error.getDefaultMessage()
                                )
                                .toList()
                )
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseApi<Void> handleDataIntegrity(
            DataIntegrityViolationException e) {

        return ResponseApi.<Void>builder()
                .status(HttpStatus.BAD_REQUEST)
                .errors(List.of(e.getMostSpecificCause().getMessage()))
                .build();
    }

    @ExceptionHandler(UnauthenticatedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseApi<Void> handleUnauthenticated(Exception e) {
        return ResponseApi.<Void>builder()
                .status(HttpStatus.UNAUTHORIZED)
                .errors(List.of(e.getMessage()))
                .build();
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseApi<String> handleDuplicateKeyException(DuplicateKeyException e) {
        return ResponseApi.<String>builder()
                .status(HttpStatus.CONFLICT)
                .errors(List.of(e.getMostSpecificCause().getMessage()))
                .build();
    }
}
