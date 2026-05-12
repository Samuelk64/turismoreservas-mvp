package turismoreservas.controllers;

import turismoreservas.domain.dto.ExperienciaRequestDTO;
import turismoreservas.domain.dto.ExperienciaResponseDTO;
import turismoreservas.service.ExperienciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/experiencias")
@Tag(name = "Experiencias", description = "CRUD de experiencias de turismo rural")
public class ExperienciaController {

    private final ExperienciaService service;

    public ExperienciaController(ExperienciaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todas las experiencias")
    public ResponseEntity<List<ExperienciaResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener experiencia por ID")
    public ResponseEntity<ExperienciaResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva experiencia")
    public ResponseEntity<ExperienciaResponseDTO> crear(
            @Valid @RequestBody ExperienciaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar experiencia existente")
    public ResponseEntity<ExperienciaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ExperienciaRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar experiencia")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}