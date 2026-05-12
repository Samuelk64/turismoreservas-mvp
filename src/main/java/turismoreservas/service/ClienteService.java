package turismoreservas.service;

import turismoreservas.domain.dto.ClienteResponseDTO;
import turismoreservas.domain.entity.Cliente;
import turismoreservas.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

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

    public List<ClienteResponseDTO> listarTodos() {
        return clientes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Cliente buscarPorId(Long id) {
        return clientes.stream()
                .filter(c -> c.getClienteId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con ID: " + id));
    }

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
