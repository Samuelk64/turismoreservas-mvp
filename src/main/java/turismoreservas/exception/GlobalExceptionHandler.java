package turismoreservas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;


/**
 * Manejador global de excepciones para la API REST.
 * <p>
 * Intercepta las excepciones lanzadas en cualquier capa de la aplicación
 * y las convierte en respuestas HTTP estructuradas mediante {@link ErrorResponse},
 * evitando que los errores internos se expongan directamente al cliente.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de tipo {@link ResourceNotFoundException}.
     * <p>
     * Se activa cuando un recurso solicitado no existe en el sistema,
     * por ejemplo al buscar una experiencia o reserva con un ID inexistente.
     * </p>
     *
     * @param ex excepción capturada con el mensaje descriptivo del recurso no encontrado
     * @return {@link ResponseEntity} con estado HTTP 404 y cuerpo {@link ErrorResponse}
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .status(404)
                        .error("Not Found")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    /**
     * Maneja excepciones de tipo {@link BusinessException}.
     * <p>
     * Se activa cuando se viola una regla de negocio del sistema,
     * como un conflicto de horarios entre experiencias (RN-05),
     * exceder la capacidad máxima de una experiencia,
     * o intentar cancelar una reserva ya cancelada.
     * </p>
     *
     * @param ex excepción capturada con el mensaje descriptivo de la regla violada
     * @return {@link ResponseEntity} con estado HTTP 400 y cuerpo {@link ErrorResponse}
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(400)
                        .error("Bad Request")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    /**
     * Maneja excepciones de validación de DTOs anotados con {@code @Valid}.
     * <p>
     * Se activa cuando los datos de entrada de una petición no cumplen
     * las restricciones definidas en los DTOs de solicitud, como campos
     * obligatorios vacíos, valores fuera de rango o formatos incorrectos.
     * Concatena todos los mensajes de error de los campos inválidos
     * en una sola cadena separada por comas.
     * </p>
     *
     * @param ex excepción capturada que contiene la lista de errores de validación por campo
     * @return {@link ResponseEntity} con estado HTTP 400 y cuerpo {@link ErrorResponse}
     *         con todos los mensajes de validación concatenados
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(400)
                        .error("Validation Error")
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
