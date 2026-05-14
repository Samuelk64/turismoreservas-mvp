package turismoreservas.service;

import turismoreservas.domain.dto.ReservaRequestDTO;
import turismoreservas.domain.dto.ReservaResponseDTO;
import turismoreservas.domain.entity.Cliente;
import turismoreservas.domain.entity.Experiencia;
import turismoreservas.domain.entity.Reserva;
import turismoreservas.domain.enums.EstadoReserva;
import turismoreservas.exception.BusinessException;
import turismoreservas.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


/**
 * Servicio encargado de la gestión de reservas de turismo rural.
 * <p>
 * Implementa la transacción principal del sistema: la creación de una reserva.
 * Este proceso incluye la validación del cliente, las experiencias seleccionadas,
 * los horarios disponibles por experiencia, la capacidad máxima de cada una,
 * la detección de conflictos de horario entre experiencias (RN-05) y el
 * cálculo automático del total a pagar.
 * </p>
 * <p>
 * Mantiene una lista de reservas en memoria durante la ejecución de la
 * aplicación. Depende de {@link ClienteService} y {@link ExperienciaService}
 * para validar y obtener las entidades relacionadas.
 * </p>
 */
@Service
public class ReservaService {

    /**
     * Contador atómico para la generación de IDs únicos de nuevas reservas.
     * Se inicializa en 1 dado que no hay reservas precargadas en el MVP.
     */
    private final AtomicLong contador = new AtomicLong(1L);

    /**
     * Lista en memoria que actúa como repositorio de reservas.
     * Se inicializa vacía — las reservas se crean en tiempo de ejecución
     * a través del endpoint de la transacción principal.
     */
    private final List<Reserva> reservas = new ArrayList<>();

    /**
     * Servicio de clientes utilizado para validar la existencia
     * del cliente al momento de crear una reserva.
     */
    private final ClienteService clienteService;

    /**
     * Servicio de experiencias utilizado para validar la existencia,
     * los horarios disponibles y la capacidad de cada experiencia
     * seleccionada en la reserva.
     */
    private final ExperienciaService experienciaService;


    /**
     * Constructor que inyecta las dependencias requeridas por el servicio.
     *
     * @param clienteService     servicio de clientes requerido para
     *                           la validación del cliente en la reserva
     * @param experienciaService servicio de experiencias requerido para
     *                           la validación de experiencias, horarios y capacidad
     */

    public ReservaService(ClienteService clienteService,
                          ExperienciaService experienciaService) {
        this.clienteService     = clienteService;
        this.experienciaService = experienciaService;
    }

    /**
     * Retorna la lista completa de reservas registradas en el sistema.
     * <p>
     * Convierte cada entidad {@link Reserva} a su representación
     * {@link ReservaResponseDTO} antes de retornarla, incluyendo
     * el mapa de horarios confirmados por experiencia.
     * </p>
     *
     * @return lista de {@link ReservaResponseDTO} con todas las reservas registradas,
     *         vacía si aún no se ha creado ninguna reserva
     */
    public List<ReservaResponseDTO> listarTodas() {
        return reservas.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca una reserva por su ID y retorna su DTO de respuesta.
     * <p>
     * Utilizado por los endpoints REST para consultas individuales
     * de reservas desde el frontend.
     * </p>
     *
     * @param id identificador único de la reserva a buscar
     * @return {@link ReservaResponseDTO} con los datos completos de la reserva,
     *         incluyendo cliente, experiencias y horarios confirmados
     * @throws ResourceNotFoundException si no existe una reserva con el ID proporcionado
     */
    public ReservaResponseDTO buscarPorId(Long id) {
        return reservas.stream()
                .filter(r -> r.getIdReserva().equals(id))
                .map(this::toDTO)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reserva no encontrada con ID: " + id));
    }

    /**
     * Ejecuta la transacción principal del sistema: la creación de una reserva.
     * <p>
     * El proceso sigue estos pasos en orden:
     * </p>
     * <ol>
     *   <li>Valida la existencia del cliente mediante {@link ClienteService}</li>
     *   <li>Valida la existencia de cada experiencia seleccionada mediante
     *       {@link ExperienciaService}</li>
     *   <li>Verifica que el horario elegido para cada experiencia esté dentro
     *       de sus horarios disponibles predefinidos</li>
     *   <li>Verifica que la cantidad de personas no supere la capacidad máxima
     *       de ninguna de las experiencias seleccionadas</li>
     *   <li>Aplica la regla de negocio RN-05: detecta conflictos de horario
     *       entre las experiencias seleccionadas comparando sus intervalos
     *       de tiempo (horaInicio, horaInicio + duracion)</li>
     *   <li>Calcula el total a pagar como la suma de los precios de todas
     *       las experiencias multiplicada por la cantidad de personas</li>
     *   <li>Construye y persiste la reserva en memoria con estado
     *       {@link EstadoReserva#CONFIRMADA} y fecha de reserva igual a
     *       la fecha actual del sistema</li>
     * </ol>
     *
     * @param dto {@link ReservaRequestDTO} con todos los datos necesarios
     *            para crear la reserva: cliente, experiencias, horarios
     *            seleccionados, fecha, cantidad de personas, método de pago
     *            y observaciones opcionales
     * @return {@link ReservaResponseDTO} con los datos completos de la reserva
     *         creada, incluyendo el ID generado, el total calculado y los
     *         horarios confirmados por experiencia
     * @throws ResourceNotFoundException si el cliente o alguna experiencia no existe
     * @throws BusinessException         si se viola alguna de las siguientes condiciones:
     *                                   el horario elegido no está disponible para la
     *                                   experiencia, la cantidad de personas supera la
     *                                   capacidad máxima, o existe un conflicto de
     *                                   horario entre experiencias (RN-05)
     */
    public ReservaResponseDTO crear(ReservaRequestDTO dto) {

        // 1. Validar cliente
        Cliente cliente = clienteService.buscarPorId(dto.getClienteId());

        // 2. Validar y obtener experiencias
        List<Experiencia> experiencias = dto.getExperienciaIds().stream()
                .map(experienciaService::buscarEntidadPorId)
                .collect(Collectors.toList());

        // 3. Validar que el horario seleccionado existe en cada experiencia
        for (Experiencia exp : experiencias) {
            String horarioElegido = dto.getHorariosSeleccionados()
                    .get(exp.getExperienciaId());
            if (horarioElegido == null) {
                throw new BusinessException(
                        "Debe seleccionar un horario para la experiencia: "
                                + exp.getNombre());
            }
            if (!exp.getHorariosDisponibles().contains(horarioElegido)) {
                throw new BusinessException(
                        "El horario '" + horarioElegido +
                                "' no está disponible para: " + exp.getNombre());
            }
        }

        // 4. Validar capacidad
        for (Experiencia exp : experiencias) {

            int personasYaReservadas = reservas.stream()
                    .filter(r -> r.getEstadoReserva() != EstadoReserva.CANCELADA)
                    .filter(r -> r.getFechaExperiencia().equals(dto.getFechaExperiencia()))
                    .filter(r -> r.getHorariosSeleccionados().equals(dto.getHorariosSeleccionados()))
                    .filter(r -> r.getExperiencias().stream()
                            .anyMatch(e -> e.getExperienciaId().equals(exp.getExperienciaId()))
                    )
                    .mapToInt(Reserva::getCantidadPersonas)
                    .sum();

            if (personasYaReservadas + dto.getCantidadPersonas() > exp.getCapacidadMaxima()) {
                throw new BusinessException(
                        "No hay cupos suficientes para la experiencia: " + exp.getNombre()
                );
            }
        }

        // 5. RN-05 — Validar conflicto de horarios entre experiencias
        for (int i = 0; i < experiencias.size(); i++) {
            for (int j = i + 1; j < experiencias.size(); j++) {
                Experiencia a = experiencias.get(i);
                Experiencia b = experiencias.get(j);

                LocalTime inicioA = LocalTime.parse(
                        dto.getHorariosSeleccionados().get(a.getExperienciaId()));
                LocalTime finA    = inicioA.plusHours(a.getDuracion());

                LocalTime inicioB = LocalTime.parse(
                        dto.getHorariosSeleccionados().get(b.getExperienciaId()));
                LocalTime finB    = inicioB.plusHours(b.getDuracion());

                boolean seSolapan = inicioA.isBefore(finB) && inicioB.isBefore(finA);

                if (seSolapan) {
                    throw new BusinessException(
                            "RN-05: Conflicto de horario entre '" + a.getNombre() +
                                    "' (" + inicioA + " - " + finA + ") y '" +
                                    b.getNombre() + "' (" + inicioB + " - " + finB + ").");
                }
            }
        }

        // 6.5. Validar conflicto con reservas existentes del mismo cliente
        List<Reserva> reservasCliente = reservas.stream()
                .filter(r -> r.getCliente().getClienteId().equals(dto.getClienteId()))
                .filter(r -> r.getEstadoReserva() != EstadoReserva.CANCELADA)
                .filter(r -> r.getFechaExperiencia().equals(dto.getFechaExperiencia()))
                .collect(Collectors.toList());

        for (Reserva reservaExistente : reservasCliente) {
            for (Experiencia expExistente : reservaExistente.getExperiencias()) {

                String horarioExistente = reservaExistente.getHorariosSeleccionados()
                        .get(expExistente.getExperienciaId());

                LocalTime inicioExistente = LocalTime.parse(horarioExistente);
                LocalTime finExistente    = inicioExistente.plusHours(expExistente.getDuracion());

                for (Experiencia expNueva : experiencias) {
                    String horarioNuevo = dto.getHorariosSeleccionados()
                            .get(expNueva.getExperienciaId());

                    LocalTime inicioNuevo = LocalTime.parse(horarioNuevo);
                    LocalTime finNuevo    = inicioNuevo.plusHours(expNueva.getDuracion());

                    boolean seSolapan = inicioNuevo.isBefore(finExistente)
                            && inicioExistente.isBefore(finNuevo);

                    if (seSolapan) {
                        throw new BusinessException(
                                "RN-05: El cliente ya tiene una reserva en esa fecha con '" +
                                        expExistente.getNombre() + "' de " + inicioExistente +
                                        " a " + finExistente + ", conflicto con '" +
                                        expNueva.getNombre() + "' de " + inicioNuevo +
                                        " a " + finNuevo + ".");
                    }
                }
            }
        }

        // 6. Calcular total
        BigDecimal total = experiencias.stream()
                .map(Experiencia::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(BigDecimal.valueOf(dto.getCantidadPersonas()));

        // 7. Crear reserva
        Reserva nueva = Reserva.builder()
                .idReserva(contador.getAndIncrement())
                .cliente(cliente)
                .experiencias(experiencias)
                .horariosSeleccionados(dto.getHorariosSeleccionados())
                .fechaReserva(LocalDate.now())
                .fechaExperiencia(dto.getFechaExperiencia())
                .cantidadPersonas(dto.getCantidadPersonas())
                .metodoPago(dto.getMetodoPago())
                .observaciones(dto.getObservaciones())
                .totalPagar(total)
                .estadoReserva(EstadoReserva.CONFIRMADA)
                .build();

        reservas.add(nueva);
        return toDTO(nueva);
    }

    /**
     * Cancela una reserva existente cambiando su estado a
     * {@link EstadoReserva#CANCELADA}.
     * <p>
     * Verifica que la reserva no esté ya en estado cancelado antes
     * de proceder, evitando operaciones redundantes y garantizando
     * la integridad del estado de la reserva.
     * </p>
     *
     * @param id identificador único de la reserva a cancelar
     * @return {@link ReservaResponseDTO} con los datos actualizados
     *         de la reserva en estado {@link EstadoReserva#CANCELADA}
     * @throws ResourceNotFoundException si no existe una reserva con el ID proporcionado
     * @throws BusinessException         si la reserva ya se encuentra en estado cancelado
     */
    public ReservaResponseDTO cancelar(Long id) {
        Reserva reserva = reservas.stream()
                .filter(r -> r.getIdReserva().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reserva no encontrada con ID: " + id));

        if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA) {
            throw new BusinessException("La reserva ya se encuentra cancelada");
        }
        reserva.setEstadoReserva(EstadoReserva.CANCELADA);
        return toDTO(reserva);
    }

    /**
            * Convierte una entidad {@link Reserva} en su DTO de respuesta.
     * <p>
     * Construye el mapa {@code horariosConfirmados} relacionando el nombre
     * de cada experiencia con su horario seleccionado, para que el frontend
     * pueda mostrar la información de forma legible sin necesidad de resolver
     * IDs adicionales.
            * </p>
            *
            * @param r entidad {@link Reserva} a convertir
     * @return {@link ReservaResponseDTO} con todos los datos de la reserva,
            *         incluyendo el mapa de horarios confirmados por nombre de experiencia
     */
    private ReservaResponseDTO toDTO(Reserva r) {
        // Construir mapa nombre experiencia -> horario
        Map<String, String> horariosConfirmados = new LinkedHashMap<>();
        r.getExperiencias().forEach(exp ->
                horariosConfirmados.put(
                        exp.getNombre(),
                        r.getHorariosSeleccionados().get(exp.getExperienciaId())
                )
        );

        return ReservaResponseDTO.builder()
                .idReserva(r.getIdReserva())
                .clienteId(r.getCliente().getClienteId())
                .clienteNombre(r.getCliente().getNombre())
                .experienciasNombres(r.getExperiencias().stream()
                        .map(Experiencia::getNombre)
                        .collect(Collectors.toList()))
                .horariosConfirmados(horariosConfirmados)
                .fechaReserva(r.getFechaReserva())
                .fechaExperiencia(r.getFechaExperiencia())
                .cantidadPersonas(r.getCantidadPersonas())
                .totalPagar(r.getTotalPagar())
                .metodoPago(r.getMetodoPago())
                .estadoReserva(r.getEstadoReserva())
                .observaciones(r.getObservaciones())
                .build();
    }
}