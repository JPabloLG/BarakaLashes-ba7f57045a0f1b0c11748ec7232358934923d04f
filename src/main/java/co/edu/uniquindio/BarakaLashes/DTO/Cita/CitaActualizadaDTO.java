package co.edu.uniquindio.BarakaLashes.DTO.Cita;

import co.edu.uniquindio.BarakaLashes.modelo.Servicio;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data Transfer Object para la respuesta después de actualizar una cita.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaActualizadaDTO {

    private Set<Servicio> servicioRequerido;
    private LocalDateTime nuevaFechaHora;
}
