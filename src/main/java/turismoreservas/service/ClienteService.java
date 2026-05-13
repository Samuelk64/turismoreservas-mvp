package turismoreservas.service;

import turismoreservas.domain.dto.ClienteResponseDTO;
import turismoreservas.domain.entity.Cliente;
import turismoreservas.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio encargado de la gestión de clientes del sistema.
 * <p>
 * Administra una lista de clientes precargados en memoria que sirven
 * como datos de prueba para el MVP. No requiere persistencia en base
 * de datos — los datos se mantienen durante la ejecución de la aplicación.
 * </p>
 * <p>
 * Es utilizado por {@link ReservaService} para validar la existencia
 * del cliente al momento de crear una reserva.
 * </p>
 */
@Service
public class ClienteService {

    /**
     * Lista en memoria que actúa como repositorio de clientes.
     * Contiene 4 clientes precargados como datos de prueba del MVP.
     */
    private final List<Cliente> clientes = new ArrayList<>(List.of(
            Cliente.builder()
                    .id(1L).clienteId(1L).nombre("Ana Garcia")
                    .correo("ana@email.com").telefono("3101234567")
                    .estado(true).fechaRegistro(LocalDate.of(2024, 1, 10)).build(),
            Cliente.builder()
                    .id(2L).clienteId(2L).nombre("Carlos Ramirez")
                    .correo("carlos@email.com").telefono("3157654321")
                    .estado(true).fechaRegistro(LocalDate.of(2024, 3, 5)).build(),
            Cliente.builder()
                    .id(3L).clienteId(3L).nombre("Maria Lopez")
                    .correo("maria@email.com").telefono("3209876543")
                    .estado(true).fechaRegistro(LocalDate.of(2024, 6, 20)).build(),
            Cliente.builder()
                    .id(4L).clienteId(4L).nombre("Juan Torres")
                    .correo("juan@email.com").telefono("3001112233")
                    .estado(true).fechaRegistro(LocalDate.of(2025, 1, 15)).build()
    ));

    /**
     * Retorna la lista completa de clientes registrados en el sistema.
     * <p>
     * Convierte cada entidad {@link Cliente} a su representación
     * {@link ClienteResponseDTO} antes de retornarla.
     * </p>
     *
     * @return lista de {@link ClienteResponseDTO} con todos los clientes disponibles
     */
    public List<ClienteResponseDTO> listarTodos() {
        return clientes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca y retorna la entidad {@link Cliente} correspondiente al ID recibido.
     * <p>
     * Este método retorna la entidad directamente (no el DTO) ya que es
     * utilizado internamente por {@link ReservaService} para construir
     * la relación entre reserva y cliente.
     * </p>
     *
     * @param id identificador único del cliente a buscar
     * @return entidad {@link Cliente} correspondiente al ID
     * @throws ResourceNotFoundException si no existe un cliente con el ID proporcionado
     */
    public Cliente buscarPorId(Long id) {
        return clientes.stream()
                .filter(c -> c.getClienteId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con ID: " + id));
    }

    /**
     * Convierte una entidad {@link Cliente} en su DTO de respuesta.
     * <p>
     * Mapea únicamente los campos necesarios para la respuesta al cliente
     * HTTP, omitiendo datos internos como {@code fechaRegistro}.
     * </p>
     *
     * @param c entidad {@link Cliente} a convertir
     * @return {@link ClienteResponseDTO} con los datos del cliente
     */
    private ClienteResponseDTO toDTO(Cliente c) {
        return ClienteResponseDTO.builder()
                .clienteId(c.getClienteId())
                .nombre(c.getNombre())
                .correo(c.getCorreo())
                .telefono(c.getTelefono())
                .estado(c.getEstado())
                .build();
    }
}
