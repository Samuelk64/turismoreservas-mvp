package turismoreservas.service;

import turismoreservas.domain.entity.OperadorRural;
import turismoreservas.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio encargado de la gestión de operadores rurales del sistema.
 * <p>
 * Administra una lista de operadores mantenida en memoria durante
 * la ejecución de la aplicación. Contiene 3 operadores precargados
 * como datos de prueba del MVP.
 * </p>
 * <p>
 * Un operador rural es la persona responsable de ofrecer y guiar
 * las experiencias de turismo rural disponibles en el sistema.
 * Su habilitación o deshabilitación determina si puede continuar
 * ofreciendo experiencias activas.
 * </p>
 */
@Service
public class OperadorService {

    /**
     * Lista en memoria que actúa como repositorio de operadores rurales.
     * Contiene 3 operadores precargados como datos de prueba del MVP,
     * cada uno con su especialidad definida.
     */
    private final List<OperadorRural> operadores = new ArrayList<>(List.of(
            OperadorRural.builder()
                    .id(1L).operadorId(1L).nombre("Pedro Arbelaez")
                    .correo("pedro@operador.com").telefono("3112345678")
                    .especialidad("Senderismo y Naturaleza")
                    .estado(true).build(),
            OperadorRural.builder()
                    .id(2L).operadorId(2L).nombre("Lucia Cardona")
                    .correo("lucia@operador.com").telefono("3124567890")
                    .especialidad("Gastronomia Rural")
                    .estado(true).build(),
            OperadorRural.builder()
                    .id(3L).operadorId(3L).nombre("Andres Salazar")
                    .correo("andres@operador.com").telefono("3156789012")
                    .especialidad("Turismo Cultural")
                    .estado(true).build()
    ));

    /**
     * Retorna la lista completa de operadores rurales registrados en el sistema.
     * <p>
     * A diferencia de otros servicios, retorna directamente las entidades
     * sin conversión a DTO dado que {@link OperadorRural} es utilizado
     * principalmente como dato de consulta en el MVP.
     * </p>
     *
     * @return lista de {@link OperadorRural} con todos los operadores disponibles
     */
    public List<OperadorRural> listarTodos() {
        return operadores;
    }


    /**
     * Busca y retorna el operador rural correspondiente al ID recibido.
     * <p>
     * Recorre la lista en memoria comparando el {@code operadorId}
     * de cada elemento con el ID proporcionado.
     * </p>
     *
     * @param id identificador único del operador a buscar
     * @return entidad {@link OperadorRural} correspondiente al ID
     * @throws ResourceNotFoundException si no existe un operador con el ID proporcionado
     */
    public OperadorRural buscarPorId(Long id) {
        return operadores.stream()
                .filter(o -> o.getOperadorId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Operador no encontrado con ID: " + id));
    }

    /**
     * Habilita un operador rural estableciendo su estado en {@code true}.
     * <p>
     * Permite que el operador vuelva a estar activo en el sistema
     * luego de haber sido deshabilitado. Verifica la existencia
     * del operador antes de modificar su estado.
     * </p>
     *
     * @param id identificador único del operador a habilitar
     * @throws ResourceNotFoundException si no existe un operador con el ID proporcionado
     */
    public void habilitar(Long id) {
        OperadorRural operador = buscarPorId(id);
        operador.setEstado(true);
    }

    /**
     * Deshabilita un operador rural estableciendo su estado en {@code false}.
     * <p>
     * Impide que el operador continúe activo en el sistema sin
     * eliminarlo definitivamente, permitiendo su reactivación posterior.
     * Verifica la existencia del operador antes de modificar su estado.
     * </p>
     *
     * @param id identificador único del operador a deshabilitar
     * @throws ResourceNotFoundException si no existe un operador con el ID proporcionado
     */
    public void deshabilitar(Long id) {
        OperadorRural operador = buscarPorId(id);
        operador.setEstado(false);
    }
}
