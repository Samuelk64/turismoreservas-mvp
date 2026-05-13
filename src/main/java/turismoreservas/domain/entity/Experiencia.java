package turismoreservas.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Experiencia {
    private Long experienciaId;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer duracion;       // en horas
    private String ubicacion;
    private String tipoExperiencia;
    private Integer capacidadMaxima;
    private Boolean estado;
    private List<String> horariosDisponibles;
}
