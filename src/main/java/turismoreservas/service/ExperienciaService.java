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

@Service
public class ExperienciaService {

    private final AtomicLong contador = new AtomicLong(5L);

    private final List<Experiencia> experiencias = new ArrayList<>(List.of(
            Experiencia.builder()
                    .experienciaId(1L).nombre("Senderismo El Roble")
                    .descripcion("Recorrido guiado por bosque nativo de 8km")
                    .precio(new BigDecimal("75000")).duracion(4)
                    .ubicacion("Vereda El Roble, Salento")
                    .tipoExperiencia("Senderismo")
                    .capacidadMaxima(12).estado(true).build(),
            Experiencia.builder()
                    .experienciaId(2L).nombre("Avistamiento de Aves")
                    .descripcion("Tour al amanecer con guia ornitologo especializado")
                    .precio(new BigDecimal("55000")).duracion(3)
                    .ubicacion("Reserva Natural Acaime, Salento")
                    .tipoExperiencia("Naturaleza")
                    .capacidadMaxima(8).estado(true).build(),
            Experiencia.builder()
                    .experienciaId(3L).nombre("Taller de Quesos Artesanales")
                    .descripcion("Elaboracion de quesos frescos con leche de la finca")
                    .precio(new BigDecimal("45000")).duracion(2)
                    .ubicacion("Finca La Esperanza, Filandia")
                    .tipoExperiencia("Gastronomia")
                    .capacidadMaxima(10).estado(true).build(),
            Experiencia.builder()
                    .experienciaId(4L).nombre("Recorrido en Jeep Willy")
                    .descripcion("Tour por paisaje cafetero en jeep tradicional")
                    .precio(new BigDecimal("35000")).duracion(2)
                    .ubicacion("Zona Cafetera, Quindio")
                    .tipoExperiencia("Cultural")
                    .capacidadMaxima(6).estado(true).build()
    ));

    public List<ExperienciaResponseDTO> listarTodas() {
        return experiencias.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ExperienciaResponseDTO buscarPorId(Long id) {
        return experiencias.stream()
                .filter(e -> e.getExperienciaId().equals(id))
                .map(this::toDTO)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Experiencia no encontrada con ID: " + id));
    }

    public Experiencia buscarEntidadPorId(Long id) {
        return experiencias.stream()
                .filter(e -> e.getExperienciaId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Experiencia no encontrada con ID: " + id));
    }

    public ExperienciaResponseDTO crear(ExperienciaRequestDTO dto) {
        Experiencia nueva = Experiencia.builder()
                .experienciaId(contador.getAndIncrement())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precio(dto.getPrecio())
                .duracion(dto.getDuracion())
                .ubicacion(dto.getUbicacion())
                .tipoExperiencia(dto.getTipoExperiencia())
                .capacidadMaxima(dto.getCapacidadMaxima())
                .estado(true)
                .build();
        experiencias.add(nueva);
        return toDTO(nueva);
    }

    public ExperienciaResponseDTO actualizar(Long id, ExperienciaRequestDTO dto) {
        Experiencia existente = buscarEntidadPorId(id);
        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setPrecio(dto.getPrecio());
        existente.setDuracion(dto.getDuracion());
        existente.setUbicacion(dto.getUbicacion());
        existente.setTipoExperiencia(dto.getTipoExperiencia());
        existente.setCapacidadMaxima(dto.getCapacidadMaxima());
        return toDTO(existente);
    }

    public void eliminar(Long id) {
        Experiencia existente = buscarEntidadPorId(id);
        existente.setEstado(false);    }

    private ExperienciaResponseDTO toDTO(Experiencia e) {
        return ExperienciaResponseDTO.builder()
                .experienciaId(e.getExperienciaId())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .precio(e.getPrecio())
                .duracion(e.getDuracion())
                .ubicacion(e.getUbicacion())
                .tipoExperiencia(e.getTipoExperiencia())
                .capacidadMaxima(e.getCapacidadMaxima())
                .estado(e.getEstado())
                .build();
    }
}
