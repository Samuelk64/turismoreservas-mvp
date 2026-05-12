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
import java.util.ArrayList;
import java.util.List;
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

        // 1. Validar y obtener cliente
        Cliente cliente = clienteService.buscarPorId(dto.getClienteId());

        // 2. Validar y obtener experiencias
        List<Experiencia> experiencias = dto.getExperienciaIds().stream()
                .map(experienciaService::buscarEntidadPorId)
                .collect(Collectors.toList());

        // 3. Verificar capacidad en cada experiencia
        experiencias.forEach(exp -> {
            if (dto.getCantidadPersonas() > exp.getCapacidadMaxima()) {
                throw new BusinessException(
                        "La experiencia '" + exp.getNombre() +
                                "' tiene capacidad maxima de " +
                                exp.getCapacidadMaxima() + " personas");
            }
        });

        // 4. Calcular total: suma de precios x cantidad de personas
        BigDecimal total = experiencias.stream()
                .map(Experiencia::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(BigDecimal.valueOf(dto.getCantidadPersonas()));

        // 5. Construir y guardar la reserva
        Reserva nueva = Reserva.builder()
                .idReserva(contador.getAndIncrement())
                .cliente(cliente)
                .experiencias(experiencias)
                .fechaReserva(LocalDate.now())
                .fechaExperiencia(dto.getFechaExperiencia())
                .horaExperiencia(dto.getHoraExperiencia())
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
        return ReservaResponseDTO.builder()
                .idReserva(r.getIdReserva())
                .clienteId(r.getCliente().getClienteId())
                .clienteNombre(r.getCliente().getNombre())
                .experienciasNombres(r.getExperiencias().stream()
                        .map(Experiencia::getNombre)
                        .collect(Collectors.toList()))
                .fechaReserva(r.getFechaReserva())
                .fechaExperiencia(r.getFechaExperiencia())
                .horaExperiencia(r.getHoraExperiencia())
                .cantidadPersonas(r.getCantidadPersonas())
                .totalPagar(r.getTotalPagar())
                .metodoPago(r.getMetodoPago())
                .estadoReserva(r.getEstadoReserva())
                .observaciones(r.getObservaciones())
                .build();
    }
}
