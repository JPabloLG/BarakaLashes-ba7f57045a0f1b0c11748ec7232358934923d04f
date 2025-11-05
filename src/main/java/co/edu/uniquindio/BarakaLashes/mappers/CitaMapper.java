package co.edu.uniquindio.BarakaLashes.mappers;

import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Cita;
import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
import co.edu.uniquindio.BarakaLashes.modelo.Servicio;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CitaMapper {

    /**
     * Convierte una entidad Cita a CitaDTO
     */
    public CitaDTO citaToCitaDTO(Cita cita) {
        if (cita == null) {
            return null;
        }

        CitaDTO dto = new CitaDTO();
        dto.setIdCita(cita.getIdCita());
        dto.setNombreCita(cita.getNombreCita());
        dto.setDescripcionCita(cita.getDescripcionCita());
        dto.setFechaCita(cita.getFechaCita());
        dto.setEstadoCita(cita.getEstadoCita());
        dto.setCancelable(
                (cita.getEstadoCita() == EstadoCita.PENDIENTE || cita.getEstadoCita() == EstadoCita.CONFIRMADA)
                        && cita.getFechaCita().isAfter(LocalDateTime.now().plusHours(24))
        );


        // Email del cliente
        if (cita.getUsuario() != null) {
            dto.setEmailCliente(cita.getUsuario().getEmail());
        }

        // Servicios como Set<Servicio> (enum)
        dto.setServiciosSeleccionados(cita.getListaServicios());

        // Servicios como List<String> para mostrar en la vista
        if (cita.getListaServicios() != null) {
            List<String> nombresServicios = cita.getListaServicios().stream()
                    .map(Servicio::name) // Los enums usan .name()
                    .collect(Collectors.toList());
            dto.setServicios(nombresServicios);
        }

        return dto;
    }

    /**
     * Convierte un CitaDTO a entidad Cita
     */
    public Cita citaDTOToCita(CitaDTO dto) {
        if (dto == null) {
            return null;
        }

        Cita cita = new Cita();
        cita.setIdCita(dto.getIdCita());
        cita.setNombreCita(dto.getNombreCita());
        cita.setDescripcionCita(dto.getDescripcionCita());
        cita.setFechaCita(dto.getFechaCita());
        cita.setEstadoCita(dto.getEstadoCita());
        cita.setListaServicios(dto.getServiciosSeleccionados());

        // El usuario se asocia en el servicio
        return cita;
    }

    /**
     * Convierte una lista de Citas a lista de CitaDTOs
     */
    public List<CitaDTO> citasToCitasDTO(List<Cita> citas) {
        if (citas == null) {
            return List.of();
        }

        return citas.stream()
                .map(this::citaToCitaDTO)
                .collect(Collectors.toList());
    }
}