package turismoreservas.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ExperienciaRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @NotNull(message = "La duracion es obligatoria")
    @Min(value = 1, message = "La duracion minima es 1 hora")
    private Integer duracion;

    @NotEmpty(message = "Debe definir al menos un horario disponible")
    private List<String> horariosDisponibles;

    @NotBlank(message = "La ubicacion es obligatoria")
    private String ubicacion;

    @NotBlank(message = "El tipo de experiencia es obligatorio")
    private String tipoExperiencia;

    @NotNull(message = "La capacidad maxima es obligatoria")
    @Min(value = 1, message = "La capacidad minima es 1 persona")
    private Integer capacidadMaxima;
}