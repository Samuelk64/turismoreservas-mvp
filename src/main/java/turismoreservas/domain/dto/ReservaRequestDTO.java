package turismoreservas.domain.dto;

import turismoreservas.domain.enums.MetodoPago;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class ReservaRequestDTO {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @NotEmpty(message = "Debe seleccionar al menos una experiencia")
    private List<Long> experienciaIds;

    @NotNull(message = "La fecha de la experiencia es obligatoria")
    @Future(message = "La fecha debe ser futura")
    private LocalDate fechaExperiencia;

    @NotNull(message = "La hora es obligatoria")
    private LocalTime horaExperiencia;

    @NotNull(message = "La cantidad de personas es obligatoria")
    @Min(value = 1, message = "Debe haber al menos 1 persona")
    private Integer cantidadPersonas;

    @NotNull(message = "El metodo de pago es obligatorio")
    private MetodoPago metodoPago;

    private String observaciones;
}
