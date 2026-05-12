package turismoreservas.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Itinerario {
    private Long idAgenda;
    private String nombre;
    private String descripcion;
    private LocalDate fechaCreacion;
    private Boolean estado;
    private List<Experiencia> experiencias;
}
