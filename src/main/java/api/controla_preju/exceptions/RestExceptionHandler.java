package api.controla_preju.exceptions;

import api.controla_preju.exceptions.responses.DefaultErrorResponse;
import api.controla_preju.exceptions.responses.ValidationErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    private ResponseEntity<DefaultErrorResponse> handleGenericException(Exception exception,
                                                                        HttpServletRequest request) {
        log.error("Erro interno não tratado na rota: {}", request.getRequestURI(), exception);

        DefaultErrorResponse response = new DefaultErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um erro inesperado no servidor.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(BusinessException.class)
    private ResponseEntity<DefaultErrorResponse> handleBusinessException(BusinessException exception,
                                                                         HttpServletRequest request) {
        DefaultErrorResponse response = new DefaultErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException exception,
                                                                              HttpServletRequest request) {
        List<ValidationErrorResponse.FieldError> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ValidationErrorResponse.FieldError(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        ValidationErrorResponse response = new ValidationErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Um ou mais campos estão inválidos.",
                request.getRequestURI(),
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(PasswordOrEmailInvalidException.class)
    private ResponseEntity<DefaultErrorResponse> handlerPasswordOrEmailException(PasswordOrEmailInvalidException exception,
                                                                                 HttpServletRequest request) {
        DefaultErrorResponse response = new DefaultErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    private ResponseEntity<DefaultErrorResponse> handlerEntityNotFoundException(EntityNotFoundException exception,
                                                                                HttpServletRequest request) {
        DefaultErrorResponse response = new DefaultErrorResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(AuthorizationException.class)
    private ResponseEntity<DefaultErrorResponse> AuthorizationException(AuthorizationException exception,
                                                                                HttpServletRequest request) {
        DefaultErrorResponse response = new DefaultErrorResponse(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                exception.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    private ResponseEntity<DefaultErrorResponse> handleRateLimitException(RateLimitExceededException exception,
                                                                          HttpServletRequest request) {
        DefaultErrorResponse response = new DefaultErrorResponse(
                Instant.now(),
                HttpStatus.TOO_MANY_REQUESTS.value(),
                exception.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
    }

}
