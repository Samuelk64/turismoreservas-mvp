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

@Service
public class ItinerarioService {

    private final AtomicLong contador = new AtomicLong(3L);
    private final ExperienciaService experienciaService;

    public ItinerarioService(ExperienciaService experienciaService) {
        this.experienciaService = experienciaService;
    }

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

    public List<Itinerario> listarTodos() {
        return itinerarios;
    }

    public Itinerario buscarPorId(Long id) {
        return itinerarios.stream()
                .filter(i -> i.getIdAgenda().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Itinerario no encontrado con ID: " + id));
    }

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
