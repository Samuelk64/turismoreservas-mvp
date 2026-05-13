package turismoreservas.service;

import turismoreservas.domain.entity.Experiencia;
import turismoreservas.domain.entity.Itinerario;
import turismoreservas.exception.BusinessException;
import turismoreservas.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Servicio encargado de la gestión de itinerarios de turismo rural.
 * <p>
 * Un itinerario agrupa una o más experiencias bajo un nombre y descripción
 * comunes, permitiendo al cliente planificar su visita de forma organizada.
 * Administra una lista de itinerarios mantenida en memoria durante la
 * ejecución de la aplicación, con 2 itinerarios precargados como datos
 * de prueba del MVP.
 * </p>
 * <p>
 * Depende de {@link ExperienciaService} para validar y obtener las
 * experiencias asociadas a cada itinerario, respetando la multiplicidad
 * {@code 1..*} definida en el diagrama de clases — un itinerario debe
 * tener siempre al menos una experiencia.
 * </p>
 */
@Service
public class ItinerarioService {

    /**
     * Contador atómico para la generación de IDs únicos de nuevos itinerarios.
     * Se inicializa en 3 dado que los datos precargados ocupan los IDs 1 y 2.
     */
    private final AtomicLong contador = new AtomicLong(3L);

    /**
     * Servicio de experiencias utilizado para validar y obtener
     * las experiencias asociadas a cada itinerario.
     */
    private final ExperienciaService experienciaService;

    /**
     * Constructor que inyecta la dependencia de {@link ExperienciaService}.
     *
     * @param experienciaService servicio de experiencias requerido
     *                           para la gestión de itinerarios
     */
    public ItinerarioService(ExperienciaService experienciaService) {
        this.experienciaService = experienciaService;
    }

    /**
     * Lista en memoria que actúa como repositorio de itinerarios.
     * Contiene 2 itinerarios precargados como datos de prueba del MVP,
     * cada uno con sus experiencias asociadas definidas.
     */
    private final List<Itinerario> itinerarios = new ArrayList<>(List.of(
            Itinerario.builder()
                    .idAgenda(1L)
                    .nombre("Fin de semana en el Quindio")
                    .descripcion("Recorrido completo por los atractivos del eje cafetero")
                    .fechaCreacion(LocalDate.of(2025, 1, 10))
                    .estado(true)
                    .experiencias(new ArrayList<>(List.of(
                            Experiencia.builder()
                                    .experienciaId(1L).nombre("Senderismo El Roble")
                                    .descripcion("Recorrido guiado por bosque nativo de 8km")
                                    .precio(new java.math.BigDecimal("75000")).duracion(4)
                                    .ubicacion("Vereda El Roble, Salento")
                                    .tipoExperiencia("Senderismo")
                                    .capacidadMaxima(12).estado(true).build(),
                            Experiencia.builder()
                                    .experienciaId(4L).nombre("Recorrido en Jeep Willy")
                                    .descripcion("Tour por paisaje cafetero en jeep tradicional")
                                    .precio(new java.math.BigDecimal("35000")).duracion(2)
                                    .ubicacion("Zona Cafetera, Quindio")
                                    .tipoExperiencia("Cultural")
                                    .capacidadMaxima(6).estado(true).build()
                    )))
                    .build(),
            Itinerario.builder()
                    .idAgenda(2L)
                    .nombre("Experiencia cafetera completa")
                    .descripcion("Naturaleza, gastronomia y cultura en un solo plan")
                    .fechaCreacion(LocalDate.of(2025, 3, 5))
                    .estado(true)
                    .experiencias(new ArrayList<>(List.of(
                            Experiencia.builder()
                                    .experienciaId(2L).nombre("Avistamiento de Aves")
                                    .descripcion("Tour al amanecer con guia ornitologo especializado")
                                    .precio(new java.math.BigDecimal("55000")).duracion(3)
                                    .ubicacion("Reserva Natural Acaime, Salento")
                                    .tipoExperiencia("Naturaleza")
                                    .capacidadMaxima(8).estado(true).build(),
                            Experiencia.builder()
                                    .experienciaId(3L).nombre("Taller de Quesos Artesanales")
                                    .descripcion("Elaboracion de quesos frescos con leche de la finca")
                                    .precio(new java.math.BigDecimal("45000")).duracion(2)
                                    .ubicacion("Finca La Esperanza, Filandia")
                                    .tipoExperiencia("Gastronomia")
                                    .capacidadMaxima(10).estado(true).build()
                    )))
                    .build()
    ));


    /**
     * Retorna la lista completa de itinerarios registrados en el sistema.
     * <p>
     * Retorna directamente las entidades sin conversión a DTO dado que
     * {@link Itinerario} es utilizado principalmente como dato de
     * consulta en el MVP.
     * </p>
     *
     * @return lista de {@link Itinerario} con todos los itinerarios disponibles,
     *         cada uno con sus experiencias asociadas
     */
    public List<Itinerario> listarTodos() {
        return itinerarios;
    }


    /**
     * Busca y retorna el itinerario correspondiente al ID recibido.
     * <p>
     * Recorre la lista en memoria comparando el {@code idAgenda}
     * de cada elemento con el ID proporcionado.
     * </p>
     *
     * @param id identificador único del itinerario a buscar
     * @return entidad {@link Itinerario} correspondiente al ID,
     *         incluyendo su lista de experiencias asociadas
     * @throws ResourceNotFoundException si no existe un itinerario con el ID proporcionado
     */
    public Itinerario buscarPorId(Long id) {
        return itinerarios.stream()
                .filter(i -> i.getIdAgenda().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Itinerario no encontrado con ID: " + id));
    }


    /**
     * Crea un nuevo itinerario con las experiencias indicadas.
     * <p>
     * Valida que la lista de IDs de experiencias no esté vacía para
     * respetar la multiplicidad {@code 1..*} del diagrama de clases.
     * Busca cada experiencia por su ID mediante {@link ExperienciaService},
     * construye el itinerario con fecha de creación actual y estado
     * {@code true} por defecto, y lo agrega a la lista en memoria.
     * </p>
     *
     * @param nombre          nombre descriptivo del itinerario
     * @param descripcion     descripción general del plan del itinerario
     * @param experienciaIds  lista de IDs de las experiencias a asociar,
     *                        debe contener al menos un elemento
     * @return entidad {@link Itinerario} creada con su ID generado
     *         y sus experiencias asociadas
     * @throws BusinessException         si la lista de experiencias está vacía
     * @throws ResourceNotFoundException si alguno de los IDs de experiencia no existe
     */
    public Itinerario crear(String nombre, String descripcion, List<Long> experienciaIds) {
        if (experienciaIds == null || experienciaIds.isEmpty()) {
            throw new BusinessException(
                    "Un itinerario debe tener al menos una experiencia");
        }
        List<Experiencia> experiencias = experienciaIds.stream()
                .map(experienciaService::buscarEntidadPorId)
                .collect(Collectors.toList());

        Itinerario nuevo = Itinerario.builder()
                .idAgenda(contador.getAndIncrement())
                .nombre(nombre)
                .descripcion(descripcion)
                .fechaCreacion(LocalDate.now())
                .estado(true)
                .experiencias(new ArrayList<>(experiencias))
                .build();

        itinerarios.add(nuevo);
        return nuevo;
    }

    /**
     * Agrega una experiencia a un itinerario existente.
     * <p>
     * Verifica que la experiencia no esté ya incluida en el itinerario
     * antes de agregarla, evitando duplicados. Obtiene la experiencia
     * mediante {@link ExperienciaService} para garantizar su existencia.
     * </p>
     *
     * @param itinerarioId  identificador único del itinerario al que se agregará la experiencia
     * @param experienciaId identificador único de la experiencia a agregar
     * @return entidad {@link Itinerario} actualizada con la nueva experiencia incluida
     * @throws ResourceNotFoundException si el itinerario o la experiencia no existen
     * @throws BusinessException         si la experiencia ya está incluida en el itinerario
     */
    public Itinerario agregarExperiencia(Long itinerarioId, Long experienciaId) {
        Itinerario itinerario = buscarPorId(itinerarioId);
        Experiencia experiencia = experienciaService.buscarEntidadPorId(experienciaId);

        boolean yaExiste = itinerario.getExperiencias().stream()
                .anyMatch(e -> e.getExperienciaId().equals(experienciaId));
        if (yaExiste) {
            throw new BusinessException(
                    "La experiencia ya existe en este itinerario");
        }

        itinerario.getExperiencias().add(experiencia);
        return itinerario;
    }

    /**
     * Elimina una experiencia de un itinerario existente.
     * <p>
     * Valida que el itinerario conserve al menos una experiencia tras
     * la eliminación, respetando la multiplicidad {@code 1..*} definida
     * en el diagrama de clases. Si el itinerario tiene una sola experiencia
     * la operación es rechazada.
     * </p>
     *
     * @param itinerarioId  identificador único del itinerario del que se eliminará la experiencia
     * @param experienciaId identificador único de la experiencia a eliminar
     * @return entidad {@link Itinerario} actualizada sin la experiencia eliminada
     * @throws ResourceNotFoundException si el itinerario no existe
     * @throws BusinessException         si el itinerario tiene una sola experiencia
     *                                   y la eliminación violaría la multiplicidad {@code 1..*}
     */
    public Itinerario eliminarExperiencia(Long itinerarioId, Long experienciaId) {
        Itinerario itinerario = buscarPorId(itinerarioId);

        if (itinerario.getExperiencias().size() == 1) {
            throw new BusinessException(
                    "El itinerario debe tener al menos una experiencia");
        }

        itinerario.getExperiencias().removeIf(
                e -> e.getExperienciaId().equals(experienciaId));
        return itinerario;
    }
}
