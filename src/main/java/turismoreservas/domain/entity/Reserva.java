package turismoreservas.domain.entity;

import turismoreservas.domain.enums.EstadoReserva;
import turismoreservas.domain.enums.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {
    private Long idReserva;
    private Cliente cliente;
    private List<Experiencia> experiencias;
    private LocalDate fechaReserva;
    private LocalDate fechaExperiencia;
    private LocalTime horaExperiencia;
    private Integer cantidadPersonas;
    private EstadoReserva estadoReserva;
    private BigDecimal totalPagar;
    private String observaciones;
    private MetodoPago metodoPago;
}
