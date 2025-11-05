package co.edu.uniquindio.BarakaLashes.DTO.Cita;


import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CitaCalendarioDTO {
    private Integer id;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private String cliente;
    private List<String> servicios;
    private EstadoCita estado;
    private String backgroundColor;
}

