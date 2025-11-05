package co.edu.uniquindio.BarakaLashes.DTO;

import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
import co.edu.uniquindio.BarakaLashes.modelo.Servicio;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Data
public class CitaDTO {
    private Integer idCita;
    private String nombreCita;
    private String descripcionCita;
    private LocalDateTime fechaCita;
    private EstadoCita estadoCita;
    private Set<Servicio> serviciosSeleccionados;
    private List<String> servicios;
    private String emailCliente;
    private boolean cancelable;

    public String getFechaFormateada() {
        if (fechaCita == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaCita.format(formatter);
    }

    public String getEstadoCitaLowerCase() {
        if (estadoCita == null) {
            return "pendiente";
        }
        return estadoCita.name().toLowerCase();
    }

    public boolean isModificable() {
        if (estadoCita == null) {
            return false;
        }
        // Solo se puede modificar si está PENDIENTE o CONFIRMADA (no cancelada ni completada)
        return estadoCita == EstadoCita.PENDIENTE || estadoCita == EstadoCita.CONFIRMADA;
    }


}