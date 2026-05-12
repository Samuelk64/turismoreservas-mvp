package turismoreservas.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Persona {
    private Long id;
    private String nombre;
    private String correo;
    private String telefono;
    private Boolean estado;

    public void actualizarDatos(String nombre, String correo, String telefono) {
        this.nombre   = nombre;
        this.correo   = correo;
        this.telefono = telefono;
    }
}
