package turismoreservas.service;

import turismoreservas.domain.dto.ExperienciaRequestDTO;
import turismoreservas.domain.dto.ExperienciaResponseDTO;
import turismoreservas.domain.entity.Experiencia;
import turismoreservas.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


/**
 * Servicio encargado de la gestión de experiencias de turismo rural.
 * <p>
 * Implementa el CRUD completo sobre una lista de experiencias mantenida
 * en memoria durante la ejecución de la aplicación. Contiene 4 experiencias
 * precargadas como datos de prueba del MVP.
 * </p>
 * <p>
 * Es utilizado por {@link ReservaService} e {@link ItinerarioService}
 * para validar y obtener experiencias al momento de crear reservas
 * e itinerarios respectivamente.
 * </p>
 */
@Service
public class ExperienciaService {

    /**
     * Contador atómico para la generación de IDs únicos de nuevas experiencias.
     * Se inicializa en 5 dado que los datos precargados ocupan los IDs del 1 al 4.
     */
    private final AtomicLong contador = new AtomicLong(5L);

    /**
     * Lista en memoria que actúa como repositorio de experiencias.
     * Contiene 4 experiencias precargadas como datos de prueba del MVP,
     * cada una con sus horarios disponibles definidos.
     */
    private final List<Experiencia> experiencias = new ArrayList<>(List.of(
            Experiencia.builder()
                    .experienciaId(1L).nombre("Senderismo El Roble")
                    .descripcion("Recorrido guiado por bosque nativo de 8km")
                    .precio(new BigDecimal("75000")).duracion(4)
                    .ubicacion("Vereda El Roble, Salento")
                    .tipoExperiencia("Senderismo")
                    .capacidadMaxima(12).estado(true)
                    .horariosDisponibles(List.of("07:00", "10:00", "13:00")).
                     build(),
            Experiencia.builder()
                    .experienciaId(2L).nombre("Avistamiento de Aves")
                    .descripcion("Tour al amanecer con guia ornitologo especializado")
                    .precio(new BigDecimal("55000")).duracion(3)
                    .ubicacion("Reserva Natural Acaime, Salento")
                    .tipoExperiencia("Naturaleza")
                    .capacidadMaxima(8).estado(true)
                    .horariosDisponibles(List.of("05:30", "08:00")).build(),
            Experiencia.builder()
                    .experienciaId(3L).nombre("Taller de Quesos Artesanales")
                    .descripcion("Elaboracion de quesos frescos con leche de la finca")
                    .precio(new BigDecimal("45000")).duracion(2)
                    .ubicacion("Finca La Esperanza, Filandia")
                    .tipoExperiencia("Gastronomia")
                    .capacidadMaxima(10).estado(true).
                    horariosDisponibles(List.of("09:00", "14:00", "16:00")).build(),
            Experiencia.builder()
                    .experienciaId(4L).nombre("Recorrido en Jeep Willy")
                    .descripcion("Tour por paisaje cafetero en jeep tradicional")
                    .precio(new BigDecimal("35000")).duracion(2)
                    .ubicacion("Zona Cafetera, Quindio")
                    .tipoExperiencia("Cultural")
                    .capacidadMaxima(6).estado(true)
                    .horariosDisponibles(List.of("08:00", "11:00", "15:00")).build()
    ));

    /**
     * Retorna la lista completa de experiencias registradas en el sistema.
     * <p>
     * Convierte cada entidad {@link Experiencia} a su representación
     * {@link ExperienciaResponseDTO} antes de retornarla.
     * </p>
     *
     * @return lista de {@link ExperienciaResponseDTO} con todas las experiencias disponibles
     */
    public List<ExperienciaResponseDTO> listarTodas() {
        return experiencias.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca una experiencia por su ID y retorna su DTO de respuesta.
     * <p>
     * Utilizado por los endpoints REST para consultas individuales
     * desde el frontend.
     * </p>
     *
     * @param id identificador único de la experiencia a buscar
     * @return {@link ExperienciaResponseDTO} con los datos de la experiencia
     * @throws ResourceNotFoundException si no existe una experiencia con el ID proporcionado
     */
    public ExperienciaResponseDTO buscarPorId(Long id) {
        return experiencias.stream()
                .filter(e -> e.getExperienciaId().equals(id))
                .map(this::toDTO)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Experiencia no encontrada con ID: " + id));
    }

    /**
     * Busca y retorna la entidad {@link Experiencia} correspondiente al ID recibido.
     * <p>
     * A diferencia de {@link #buscarPorId(Long)}, este método retorna la entidad
     * directamente sin convertirla a DTO. Es utilizado internamente por
     * {@link ReservaService} e {@link ItinerarioService} para construir
     * sus respectivas relaciones con la experiencia.
     * </p>
     *
     * @param id identificador único de la experiencia a buscar
     * @return entidad {@link Experiencia} correspondiente al ID
     * @throws ResourceNotFoundException si no existe una experiencia con el ID proporcionado
     */
    public Experiencia buscarEntidadPorId(Long id) {
        return experiencias.stream()
                .filter(e -> e.getExperienciaId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Experiencia no encontrada con ID: " + id));
    }


    /**
     * Crea una nueva experiencia a partir del DTO de solicitud recibido.
     * <p>
     * Genera un ID único mediante el contador atómico, construye la entidad
     * con estado {@code true} por defecto y la agrega a la lista en memoria.
     * </p>
     *
     * @param dto {@link ExperienciaRequestDTO} con los datos de la nueva experiencia
     * @return {@link ExperienciaResponseDTO} con los datos de la experiencia creada,
     *         incluyendo el ID generado automáticamente
     */
    public ExperienciaResponseDTO crear(ExperienciaRequestDTO dto) {
        Experiencia nueva = Experiencia.builder()
                .experienciaId(contador.getAndIncrement())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precio(dto.getPrecio())
                .duracion(dto.getDuracion())
                .horariosDisponibles(dto.getHorariosDisponibles())
                .ubicacion(dto.getUbicacion())
                .tipoExperiencia(dto.getTipoExperiencia())
                .capacidadMaxima(dto.getCapacidadMaxima())
                .estado(true)
                .build();
        experiencias.add(nueva);
        return toDTO(nueva);
    }

    /**
     * Actualiza todos los campos de una experiencia existente identificada por su ID.
     * <p>
     * Localiza la entidad en memoria y reemplaza sus atributos con los
     * valores recibidos en el DTO. Al operar sobre la referencia directa
     * del objeto en la lista, los cambios se reflejan inmediatamente.
     * </p>
     *
     * @param id  identificador único de la experiencia a actualizar
     * @param dto {@link ExperienciaRequestDTO} con los nuevos valores de los campos
     * @return {@link ExperienciaResponseDTO} con los datos actualizados de la experiencia
     * @throws ResourceNotFoundException si no existe una experiencia con el ID proporcionado
     */
    public ExperienciaResponseDTO actualizar(Long id, ExperienciaRequestDTO dto) {
        Experiencia existente = buscarEntidadPorId(id);
        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setPrecio(dto.getPrecio());
        existente.setDuracion(dto.getDuracion());
        existente.setHorariosDisponibles(dto.getHorariosDisponibles());
        existente.setUbicacion(dto.getUbicacion());
        existente.setTipoExperiencia(dto.getTipoExperiencia());
        existente.setCapacidadMaxima(dto.getCapacidadMaxima());
        return toDTO(existente);
    }

    /**
     * Elimina una experiencia de la lista en memoria identificada por su ID.
     * <p>
     * Verifica primero la existencia de la experiencia antes de proceder
     * con la eliminación, garantizando una respuesta de error apropiada
     * si el ID no existe.
     * </p>
     *
     * @param id identificador único de la experiencia a eliminar
     * @throws ResourceNotFoundException si no existe una experiencia con el ID proporcionado
     */
    public void eliminar(Long id) {
        Experiencia existente = buscarEntidadPorId(id);
        experiencias.remove(existente);
    }


    /**
     * Convierte una entidad {@link Experiencia} en su DTO de respuesta.
     * <p>
     * Mapea todos los campos de la entidad al {@link ExperienciaResponseDTO},
     * incluyendo los horarios disponibles para ser consumidos por el frontend.
     * </p>
     *
     * @param e entidad {@link Experiencia} a convertir
     * @return {@link ExperienciaResponseDTO} con todos los datos de la experiencia
     */
    private ExperienciaResponseDTO toDTO(Experiencia e) {
        return ExperienciaResponseDTO.builder()
                .experienciaId(e.getExperienciaId())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .precio(e.getPrecio())
                .duracion(e.getDuracion())
                .horariosDisponibles(e.getHorariosDisponibles())
                .ubicacion(e.getUbicacion())
                .tipoExperiencia(e.getTipoExperiencia())
                .capacidadMaxima(e.getCapacidadMaxima())
                .estado(e.getEstado())
                .build();
    }
}
