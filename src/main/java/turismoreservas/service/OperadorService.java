package turismoreservas.service;

import turismoreservas.domain.entity.OperadorRural;
import turismoreservas.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OperadorService {

    private final List<OperadorRural> operadores = new ArrayList<>(List.of(
            OperadorRural.builder()
                    .id(1L).operadorId(1L).nombre("Pedro Arbelaez")
                    .correo("pedro@operador.com").telefono("3112345678")
                    .especialidad("Senderismo y Naturaleza")
                    .estado(true).build(),
            OperadorRural.builder()
                    .id(2L).operadorId(2L).nombre("Lucia Cardona")
                    .correo("lucia@operador.com").telefono("3124567890")
                    .especialidad("Gastronomia Rural")
                    .estado(true).build(),
            OperadorRural.builder()
                    .id(3L).operadorId(3L).nombre("Andres Salazar")
                    .correo("andres@operador.com").telefono("3156789012")
                    .especialidad("Turismo Cultural")
                    .estado(true).build()
    ));

    public List<OperadorRural> listarTodos() {
        return operadores;
    }

    public OperadorRural buscarPorId(Long id) {
        return operadores.stream()
                .filter(o -> o.getOperadorId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Operador no encontrado con ID: " + id));
    }

    public void habilitar(Long id) {
        OperadorRural operador = buscarPorId(id);
        operador.setEstado(true);
    }

    public void deshabilitar(Long id) {
        OperadorRural operador = buscarPorId(id);
        operador.setEstado(false);
    }
}
