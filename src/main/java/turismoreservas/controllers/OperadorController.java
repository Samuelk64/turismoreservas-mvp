package turismoreservas.controllers;

import turismoreservas.domain.entity.OperadorRural;
import turismoreservas.service.OperadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operadores")
@Tag(name = "Operadores", description = "Gestion de operadores rurales")
public class OperadorController {

    private final OperadorService service;

    public OperadorController(OperadorService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos los operadores")
    public ResponseEntity<List<OperadorRural>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener operador por ID")
    public ResponseEntity<OperadorRural> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}/habilitar")
    @Operation(summary = "Habilitar operador")
    public ResponseEntity<Void> habilitar(@PathVariable Long id) {
        service.habilitar(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/deshabilitar")
    @Operation(summary = "Deshabilitar operador")
    public ResponseEntity<Void> deshabilitar(@PathVariable Long id) {
        service.deshabilitar(id);
        return ResponseEntity.ok().build();
    }
}
