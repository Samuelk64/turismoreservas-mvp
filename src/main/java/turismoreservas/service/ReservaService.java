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

@Service
public class ReservaService {

    private final AtomicLong contador = new AtomicLong(1L);
    private final List<Reserva> reservas = new ArrayList<>();

    private final ClienteService clienteService;
    private final ExperienciaService experienciaService;

    public ReservaService(ClienteService clienteService,
                          ExperienciaService experienciaService) {
        this.clienteService     = clienteService;
        this.experienciaService = experienciaService;
    }

    public List<ReservaResponseDTO> listarTodas() {
        return reservas.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ReservaResponseDTO buscarPorId(Long id) {
        return reservas.stream()
                .filter(r -> r.getIdReserva().equals(id))
                .map(this::toDTO)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reserva no encontrada con ID: " + id));
    }

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
        experiencias.forEach(exp -> {
            if (dto.getCantidadPersonas() > exp.getCapacidadMaxima()) {
                throw new BusinessException(
                        "La experiencia '" + exp.getNombre() +
                                "' tiene capacidad maxima de " +
                                exp.getCapacidadMaxima() + " personas");
            }
        });

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