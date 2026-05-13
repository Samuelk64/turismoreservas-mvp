package turismoreservas.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Clase base abstracta que representa a cualquier persona
 * dentro del sistema de gestión de reservas de turismo rural.
 * <p>
 * Contiene los atributos y comportamientos comunes compartidos
 * por {@link Cliente}, {@link OperadorRural}
 * </p>
 */

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

    /**
     * Actualiza los datos de contacto de la persona.
     * <p>
     * Modifica directamente los atributos {@code nombre},
     * {@code correo} y {@code telefono} con los valores recibidos.
     * </p>
     *
     * @param nombre   nuevo nombre completo de la persona
     * @param correo   nuevo correo electrónico de la persona
     * @param telefono nuevo número de teléfono de la persona
     */

    public void actualizarDatos(String nombre, String correo, String telefono) {
        this.nombre   = nombre;
        this.correo   = correo;
        this.telefono = telefono;
    }
}
