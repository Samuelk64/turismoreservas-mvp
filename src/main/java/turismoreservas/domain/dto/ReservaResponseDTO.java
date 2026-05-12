package turismoreservas.domain.dto;

import turismoreservas.domain.enums.EstadoReserva;
import turismoreservas.domain.enums.MetodoPago;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class ReservaResponseDTO {
    private Long idReserva;
    private Long clienteId;
    private String clienteNombre;
    private List<String> experienciasNombres;
    private LocalDate fechaReserva;
    private LocalDate fechaExperiencia;
    private LocalTime horaExperiencia;
    private Integer cantidadPersonas;
    private BigDecimal totalPagar;
    private MetodoPago metodoPago;
    private EstadoReserva estadoReserva;
    private String observaciones;
}