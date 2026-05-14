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
            ,
            Cliente.builder()
                    .id(5L).clienteId(5L).nombre("Laura Martinez")
                    .correo("laura@email.com").telefono("3114455667")
                    .estado(true).fechaRegistro(LocalDate.of(2025, 2, 10)).build(),

            Cliente.builder()
                    .id(6L).clienteId(6L).nombre("Andres Gomez")
                    .correo("andres@email.com").telefono("3127788990")
                    .estado(true).fechaRegistro(LocalDate.of(2025, 2, 22)).build(),

            Cliente.builder()
                    .id(7L).clienteId(7L).nombre("Sofia Herrera")
                    .correo("sofia@email.com").telefono("3205566778")
                    .estado(true).fechaRegistro(LocalDate.of(2025, 3, 1)).build(),

            Cliente.builder()
                    .id(8L).clienteId(8L).nombre("Felipe Castro")
                    .correo("felipe@email.com").telefono("3009988776")
                    .estado(true).fechaRegistro(LocalDate.of(2025, 3, 18)).build(),

            Cliente.builder()
                    .id(9L).clienteId(9L).nombre("Valentina Rojas")
                    .correo("valentina@email.com").telefono("3156677889")
                    .estado(true).fechaRegistro(LocalDate.of(2025, 4, 5)).build()
            ,
            Cliente.builder()
                    .id(10L).clienteId(10L).nombre("Camila Vargas")
                    .correo("camila@email.com").telefono("3184455661")
                    .estado(true).fechaRegistro(LocalDate.of(2025, 4, 12)).build(),

            Cliente.builder()
                    .id(11L).clienteId(11L).nombre("Sebastian Molina")
                    .correo("sebastian@email.com").telefono("3132244668")
                    .estado(true).fechaRegistro(LocalDate.of(2025, 4, 25)).build(),

            Cliente.builder()
                    .id(12L).clienteId(12L).nombre("Daniela Quintero")
                    .correo("daniela@email.com").telefono("3204455779")
                    .estado(true).fechaRegistro(LocalDate.of(2025, 5, 2)).build(),

            Cliente.builder()
                    .id(13L).clienteId(13L).nombre("Miguel Fernandez")
                    .correo("miguel@email.com").telefono("3018899775")
                    .estado(true).fechaRegistro(LocalDate.of(2025, 5, 10)).build(),

            Cliente.builder()
                    .id(14L).clienteId(14L).nombre("Paula Jimenez")
                    .correo("paula@email.com").telefono("3167788994")
                    .estado(true).fechaRegistro(LocalDate.of(2025, 5, 18)).build()
            ,
            Cliente.builder()
                    .id(15L).clienteId(15L).nombre("Natalia Castaño")
                    .correo("natalia@email.com").telefono("3105566442")
                    .estado(true).fechaRegistro(LocalDate.of(2025, 5, 25)).build(),

            Cliente.builder()
                    .id(16L).clienteId(16L).nombre("David Restrepo")
                    .correo("david@email.com").telefono("3159988441")
                    .estado(true).fechaRegistro(LocalDate.of(2025, 6, 3)).build(),

            Cliente.builder()
                    .id(17L).clienteId(17L).nombre("Juliana Moreno")
                    .correo("juliana@email.com").telefono("3123344556")
                    .estado(true).fechaRegistro(LocalDate.of(2025, 6, 11)).build(),

            Cliente.builder()
                    .id(18L).clienteId(18L).nombre("Esteban Salazar")
                    .correo("esteban@email.com").telefono("3201122334")
                    .estado(true).fechaRegistro(LocalDate.of(2025, 6, 20)).build(),

            Cliente.builder()
                    .id(19L).clienteId(19L).nombre("Carolina Mejia")
                    .correo("carolina@email.com").telefono("3015566772")
                    .estado(true).fechaRegistro(LocalDate.of(2025, 7, 1)).build()
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
