package turismoreservas.controllers;

import turismoreservas.domain.entity.Itinerario;
import turismoreservas.service.ItinerarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itinerarios")
@Tag(name = "Itinerarios", description = "Gestion de itinerarios de turismo rural")
public class ItinerarioController {

    private final ItinerarioService service;

    public ItinerarioController(ItinerarioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos los itinerarios")
    public ResponseEntity<List<Itinerario>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener itinerario por ID")
    public ResponseEntity<Itinerario> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo itinerario")
    public ResponseEntity<Itinerario> crear(
            @RequestParam @NotBlank String nombre,
            @RequestParam @NotBlank String descripcion,
            @RequestParam @NotEmpty List<Long> experienciaIds) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.crear(nombre, descripcion, experienciaIds));
    }

    @PutMapping("/{id}/agregar-experiencia/{experienciaId}")
    @Operation(summary = "Agregar experiencia a un itinerario")
    public ResponseEntity<Itinerario> agregarExperiencia(
            @PathVariable Long id,
            @PathVariable Long experienciaId) {
        return ResponseEntity.ok(service.agregarExperiencia(id, experienciaId));
    }

    @DeleteMapping("/{id}/eliminar-experiencia/{experienciaId}")
    @Operation(summary = "Eliminar experiencia de un itinerario")
    public ResponseEntity<Itinerario> eliminarExperiencia(
            @PathVariable Long id,
            @PathVariable Long experienciaId) {
        return ResponseEntity.ok(service.eliminarExperiencia(id, experienciaId));
    }
}
