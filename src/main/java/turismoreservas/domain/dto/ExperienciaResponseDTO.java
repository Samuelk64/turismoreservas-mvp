package turismoreservas.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ExperienciaResponseDTO {
    private Long experienciaId;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer duracion;
    private List<String> horariosDisponibles;
    private String ubicacion;
    private String tipoExperiencia;
    private Integer capacidadMaxima;
    private Boolean estado;
}