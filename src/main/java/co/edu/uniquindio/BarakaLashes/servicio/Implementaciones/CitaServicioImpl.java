package co.edu.uniquindio.BarakaLashes.servicio.Implementaciones;

import co.edu.uniquindio.BarakaLashes.DTO.Cita.CitaActualizadaDTO;
import co.edu.uniquindio.BarakaLashes.DTO.Cita.CitaCalendarioDTO;
import co.edu.uniquindio.BarakaLashes.DTO.Cita.ResumenCitasDTO;
import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.DTO.EmailDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Cita;
import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
import co.edu.uniquindio.BarakaLashes.modelo.Servicio;
import co.edu.uniquindio.BarakaLashes.modelo.Usuario;
import co.edu.uniquindio.BarakaLashes.repositorio.CitaRepositorio;
import co.edu.uniquindio.BarakaLashes.repositorio.UsuarioRepositorio;
import co.edu.uniquindio.BarakaLashes.mappers.CitaMapper;
import co.edu.uniquindio.BarakaLashes.servicio.EmailServicio;
import lombok.extern.slf4j.Slf4j;
import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Collate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor

public class CitaServicioImpl implements CitaServicio {

    private final CitaRepositorio citaRepo;
    private final CitaMapper citaMapper;
    private final UsuarioRepositorio usuarioRepositorio;
    private final EmailServicio emailService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public int crearCita(CitaDTO citaDTO) throws Exception {
        log.info("=== INTENTANDO CREAR CITA ===");
        log.info("Email cliente: {}", citaDTO.getEmailCliente());
        log.info("Nombre cita: {}", citaDTO.getNombreCita());
        log.info("Fecha cita: {}", citaDTO.getFechaCita());
        log.info("Servicios seleccionados: {}", citaDTO.getServiciosSeleccionados());

        try {
            // Validar datos básicos
            if (citaDTO.getEmailCliente() == null || citaDTO.getEmailCliente().trim().isEmpty()) {
                throw new Exception("El email del cliente es obligatorio");
            }

            if (citaDTO.getNombreCita() == null || citaDTO.getNombreCita().trim().isEmpty()) {
                throw new Exception("El motivo de la cita es obligatorio");
            }

            if (citaDTO.getFechaCita() == null) {
                throw new Exception("La fecha de la cita es obligatoria");
            }

            if (citaDTO.getServiciosSeleccionados() == null || citaDTO.getServiciosSeleccionados().isEmpty()) {
                throw new Exception("Debe seleccionar al menos un servicio");
            }


            Cita cita = citaMapper.citaDTOToCita(citaDTO);
            log.info("Cita mapeada: {}", cita);

            // Asociar el usuario a partir del email del cliente
            Usuario usuario = usuarioRepositorio.findByEmail(citaDTO.getEmailCliente())
                    .orElseThrow(() -> new Exception("No existe un usuario registrado con el email proporcionado"));
            cita.setUsuario(usuario);

            // Estado inicial por defecto
            cita.setEstadoCita(EstadoCita.PENDIENTE);


            log.info("Guardando cita en la base de datos...");
            Cita citaGuardada = citaRepo.save(cita);
            log.info("CITA CREADA EXITOSAMENTE - ID: {}", citaGuardada.getIdCita());

            return citaGuardada.getIdCita();

        } catch (Exception e) {
            log.error(" ERROR AL CREAR CITA: {}", e.getMessage());
            log.error("Stack trace:", e);
            throw e;
        }
    }

    @Override
    public int actualizarCita(int idCita, CitaDTO citaDTO) throws Exception {
        // Verificar que la cita existe
        Optional<Cita> citaOptional = citaRepo.findById(idCita);
        if (citaOptional.isEmpty()) {
            throw new Exception("Cita no encontrada con ID: " + idCita);
        }

        Cita cita = citaOptional.get();

        cita.setNombreCita(citaDTO.getNombreCita());
        cita.setDescripcionCita(citaDTO.getDescripcionCita());
        cita.setFechaCita(citaDTO.getFechaCita());
        cita.setEstadoCita(citaDTO.getEstadoCita());
        cita.setListaServicios(citaDTO.getServiciosSeleccionados());


        citaRepo.save(cita);
        return cita.getIdCita();
    }

    @Override
    public int modificarCita(int idCita, CitaActualizadaDTO citaDTO) throws Exception {
        // Verificar que la cita existe
        Optional<Cita> citaOptional = citaRepo.findById(idCita);
        if (citaOptional.isEmpty()) {
            throw new Exception("Cita no encontrada con ID: " + idCita);
        }

        Cita cita = citaOptional.get();

        cita.setFechaCita(citaDTO.getNuevaFechaHora());
        cita.setListaServicios(citaDTO.getServicioRequerido());

        citaRepo.save(cita);
        return cita.getIdCita();
    }

    @Override
    public int eliminarCita(int idCita) throws Exception {

        if (!citaRepo.existsById(idCita)) {
            throw new Exception("Cita a eliminar no encontrada.");
        }

        citaRepo.deleteById(idCita);
        return idCita;
    }

    @Override
    public CitaDTO obtenerCita(int idCita) throws Exception {
        Optional<Cita> citaOptional = citaRepo.findById(idCita);
        if (citaOptional.isEmpty()) {
            throw new Exception("Cita no encontrada con ID: " + idCita);
        }

        return citaMapper.citaToCitaDTO(citaOptional.get());
    }

    @Override
    public List<CitaDTO> listarCitas() {
        List<Cita> citas = citaRepo.findAll();
        return citaMapper.citasToCitasDTO(citas);
    }

    @Override
    public List<CitaDTO> listarCitasPorUsuario(int idUsuario) {
        // Busca directamente en la BD por el ID del usuario
        List<Cita> citas = citaRepo.findByUsuarioIdUsuario(idUsuario);
        return citaMapper.citasToCitasDTO(citas);
    }

    @Override
    public List<CitaDTO> listarCitasPorUsuarioEmail(String emailUsuario) {
        log.info("=== BUSCANDO CITAS PARA EMAIL: {} ===", emailUsuario);

        // OPCIÓN 1: Buscar por email directamente si tienes el campo en Cita
        List<Cita> todasLasCitas = citaRepo.findAll();

        // Debug: mostrar todas las citas
        log.info("=== TOTAL CITAS EN BD: {} ===", todasLasCitas.size());
        for (Cita cita : todasLasCitas) {
            log.info("Cita ID: {}, Nombre: {}, Usuario: {}",
                    cita.getIdCita(),
                    cita.getNombreCita(),
                    cita.getUsuario() != null ? cita.getUsuario().getEmail() : "NULL");
        }

        // Filtrar citas - prueba diferentes enfoques:
        List<Cita> citasFiltradas = todasLasCitas.stream()
                .filter(cita -> {
                    // Si la cita tiene usuario asociado, comparar emails
                    if (cita.getUsuario() != null && cita.getUsuario().getEmail() != null) {
                        boolean match = cita.getUsuario().getEmail().equals(emailUsuario);
                        if (match) {
                            log.info("CITA ENCONTRADA POR USUARIO - ID: {}, Email: {}",
                                    cita.getIdCita(), cita.getUsuario().getEmail());
                        }
                        return match;
                    }
                    return false;
                })
                .toList();

        log.info("=== CITAS FILTRADAS ENCONTRADAS: {} ===", citasFiltradas.size());
        return citaMapper.citasToCitasDTO(citasFiltradas);
    }

    @Override
    public int cancelarCita(int idCita) throws Exception {
        Cita cita = citaRepo.findCancelableCita(idCita)
                .orElseThrow(() -> new Exception("La cita no existe o no se puede cancelar en su estado actual"));

        // Regla: solo cancelar con 24h de anticipación
        if (!java.time.LocalDateTime.now().isBefore(cita.getFechaCita().minusHours(24))) {
            throw new Exception("Solo puedes cancelar con 24 horas de anticipación");
        }

        citaRepo.actualizarEstadoCita(idCita, EstadoCita.CANCELADA);
        return idCita;
    }


    @Override
    public List<CitaDTO> obtenerHistorialCitas(String emailUsuario) throws Exception {
        log.info("=== OBTENIENDO HISTORIAL DE CITAS PARA: {} ===", emailUsuario);

        // Verificar que el usuario existe
        if (!usuarioRepositorio.existsByEmail(emailUsuario)) {
            throw new Exception("Usuario no encontrado: " + emailUsuario);
        }

        // Obtener todas las citas del usuario
        List<Cita> citas = citaRepo.findByUsuarioEmail(emailUsuario);

        log.info("=== CITAS ENCONTRADAS EN HISTORIAL: {} ===", citas.size());

        // Log para debug
        for (Cita cita : citas) {
            log.info("Cita ID: {}, Nombre: {}, Fecha: {}, Estado: {}",
                    cita.getIdCita(), cita.getNombreCita(), cita.getFechaCita(), cita.getEstadoCita());
        }

        return citas.stream()
                .map(citaMapper::citaToCitaDTO)
                .collect(Collectors.toList());

    }

    @Override
    public List<CitaDTO> obtenerCitasPorRango(LocalDate fechaInicio, LocalDate fechaFin,
                                              Integer idUsuario, Integer idEmpleado) throws Exception {

        log.info("Consultando citas por rango: {} a {}, usuario: {}, empleado: {}",
                fechaInicio, fechaFin, idUsuario, idEmpleado);

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);

        List<Cita> citas;

        if (idUsuario != null && idEmpleado != null) {
            // Usar el método correcto del repositorio
            citas = citaRepo.findByFechaCitaBetweenAndUsuarioIdUsuarioAndNegocioIdNegocio(
                    inicio, fin, idUsuario, idEmpleado);
        } else if (idUsuario != null) {
            citas = citaRepo.findByFechaCitaBetweenAndUsuarioIdUsuario(inicio, fin, idUsuario);
        } else if (idEmpleado != null) {
            citas = citaRepo.findByFechaCitaBetweenAndNegocioIdNegocio(inicio, fin, idEmpleado);
        } else {
            citas = citaRepo.findByFechaCitaBetween(inicio, fin);
        }

        return convertirCitasADTO(citas);
    }

    @Override
    public List<CitaCalendarioDTO> obtenerCitasParaCalendario(LocalDate fechaInicio, LocalDate fechaFin,
                                                              Integer idUsuario, Integer idEmpleado) throws Exception {

        List<CitaDTO> citas = obtenerCitasPorRango(fechaInicio, fechaFin, idUsuario, idEmpleado);

        return citas.stream()
                .map(this::convertirACalendarioDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocalDateTime> obtenerHorariosDisponibles(LocalDate fecha, Integer idEmpleado) throws Exception {
        log.info("Consultando horarios disponibles para fecha: {}, empleado: {}", fecha, idEmpleado);

        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(23, 59, 59);

        // Usar el método correcto del repositorio
        List<Cita> citasExistentes = citaRepo.findByFechaCitaBetweenAndNegocioIdNegocio(
                inicioDia, finDia, idEmpleado);

        List<LocalDateTime> horariosDisponibles = generarHorariosDelDia(fecha);

        return horariosDisponibles.stream()
                .filter(horario -> !estaOcupado(horario, citasExistentes))
                .collect(Collectors.toList());
    }

    @Override
    public ResumenCitasDTO obtenerResumenCitas(LocalDate fechaInicio, LocalDate fechaFin, Integer idEmpleado) throws Exception {
        List<CitaDTO> citas = obtenerCitasPorRango(fechaInicio, fechaFin, null, idEmpleado);

        long totalCitas = citas.size();
        long citasConfirmadas = citas.stream()
                .filter(c -> c.getEstadoCita() == EstadoCita.CONFIRMADA)
                .count();
        long citasCanceladas = citas.stream()
                .filter(c -> c.getEstadoCita() == EstadoCita.CANCELADA)
                .count();
        long citasCompletadas = citas.stream()
                .filter(c -> c.getEstadoCita() == EstadoCita.COMPLETADA)
                .count();

        return ResumenCitasDTO.builder()
                .totalCitas(totalCitas)
                .citasConfirmadas(citasConfirmadas)
                .citasCanceladas(citasCanceladas)
                .citasCompletadas(citasCompletadas)
                .citasPendientes(totalCitas - citasConfirmadas - citasCanceladas - citasCompletadas)
                .build();
    }

    // 🔧 MÉTODOS AUXILIARES PRIVADOS

    private List<CitaDTO> convertirCitasADTO(List<Cita> citas) {
        return citas.stream()
                .map(this::convertirCitaADTO)
                .collect(Collectors.toList());
    }

    private CitaDTO convertirCitaADTO(Cita cita) {
        CitaDTO dto = new CitaDTO();
        dto.setIdCita(cita.getIdCita());
        dto.setNombreCita(cita.getNombreCita());
        dto.setDescripcionCita(cita.getDescripcionCita());
        dto.setFechaCita(cita.getFechaCita());
        dto.setEstadoCita(cita.getEstadoCita());
        dto.setServiciosSeleccionados(cita.getListaServicios());

        if (cita.getUsuario() != null) {
            dto.setEmailCliente(cita.getUsuario().getEmail());
        }

        dto.setCancelable(cita.getEstadoCita() == EstadoCita.PENDIENTE ||
                cita.getEstadoCita() == EstadoCita.CONFIRMADA);
        return dto;
    }

    private CitaCalendarioDTO convertirACalendarioDTO(CitaDTO cita) {
        return CitaCalendarioDTO.builder()
                .id(cita.getIdCita())
                .title(cita.getNombreCita())
                .start(cita.getFechaCita())
                .end(cita.getFechaCita().plusHours(1))
                .cliente(cita.getEmailCliente())
                .servicios(cita.getServiciosSeleccionados() != null ?
                        cita.getServiciosSeleccionados().stream()
                                .map(Enum::name)
                                .collect(Collectors.toList()) : Collections.emptyList())
                .estado(cita.getEstadoCita())
                .backgroundColor(obtenerColorPorEstado(cita.getEstadoCita()))
                .build();
    }

    private String obtenerColorPorEstado(EstadoCita estado) {
        if (estado == null) return "#6c757d";

        switch (estado) {
            case CONFIRMADA: return "#28a745";
            case PENDIENTE: return "#ffc107";
            case CANCELADA: return "#dc3545";
            case COMPLETADA: return "#17a2b8";
            default: return "#6c757d";
        }
    }

    private List<LocalDateTime> generarHorariosDelDia(LocalDate fecha) {
        List<LocalDateTime> horarios = new ArrayList<>();
        LocalDateTime horario = fecha.atTime(9, 0);

        while (horario.getHour() < 18) {
            horarios.add(horario);
            horario = horario.plusMinutes(30);
        }

        return horarios;
    }

    private boolean estaOcupado(LocalDateTime horario, List<Cita> citasExistentes) {
        return citasExistentes.stream()
                .anyMatch(cita -> {
                    // Verificar si el horario coincide (considerando duración de la cita)
                    LocalDateTime inicioCita = cita.getFechaCita();
                    LocalDateTime finCita = inicioCita.plusHours(1); // Asumiendo 1 hora de duración

                    return !horario.isBefore(inicioCita) && horario.isBefore(finCita) &&
                            cita.getEstadoCita() != EstadoCita.CANCELADA;
                });
    }


    @Override
    public List<CitaDTO> obtenerCitasPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws Exception {
        log.info("=== OBTENIENDO CITAS POR RANGO: {} - {} ===", fechaInicio, fechaFin);

        // USAR MÉTODO QUE SÍ EXISTE
        List<Cita> citas = citaRepo.findByFechaCitaBetween(fechaInicio, fechaFin);
        log.info("Citas encontradas en el rango: {}", citas.size());

        return convertirCitasADTO(citas); // Usar tu método auxiliar
    }

    @Override
    public List<CitaDTO> obtenerCitasConFiltros(LocalDateTime fechaInicio, LocalDateTime fechaFin,
                                                String emailCliente, EstadoCita estado) throws Exception {
        log.info("=== OBTENIENDO CITAS CON FILTROS ===");
        log.info("Rango: {} - {}", fechaInicio, fechaFin);
        log.info("Cliente: {}, Estado: {}", emailCliente, estado);

        List<Cita> citas;

        // IMPLEMENTAR FILTROS USANDO MÉTODOS EXISTENTES
        if (emailCliente != null && estado != null) {
            // Filtrar por email y estado
            citas = citaRepo.findByUsuarioEmailAndEstadoCita(emailCliente, estado);
            // Luego filtrar por fecha
            citas = citas.stream()
                    .filter(c -> !c.getFechaCita().isBefore(fechaInicio) && !c.getFechaCita().isAfter(fechaFin))
                    .collect(Collectors.toList());
        } else if (emailCliente != null) {
            // Solo filtrar por email
            citas = citaRepo.findByUsuarioEmail(emailCliente);
            citas = citas.stream()
                    .filter(c -> !c.getFechaCita().isBefore(fechaInicio) && !c.getFechaCita().isAfter(fechaFin))
                    .collect(Collectors.toList());
        } else if (estado != null) {
            // Solo filtrar por estado y fecha
            citas = citaRepo.findByEstadoCitaAndFechaCitaBetween(estado, fechaInicio, fechaFin);
        } else {
            // Solo por fecha
            citas = citaRepo.findByFechaCitaBetween(fechaInicio, fechaFin);
        }

        log.info("Citas encontradas: {}", citas.size());
        return convertirCitasADTO(citas);
    }

    @Override
    public List<CitaDTO> buscarCitasPorCliente(String busqueda) throws Exception {
        log.info("=== BUSCANDO CITAS POR CLIENTE: {} ===", busqueda);

        // BUSCAR POR EMAIL DEL CLIENTE (método que existe)
        List<Cita> citas = citaRepo.findByUsuarioEmail(busqueda);
        log.info("Citas encontradas: {}", citas.size());

        return convertirCitasADTO(citas);
    }

    @Override
    public List<CitaDTO> buscarCitasPorServicio(Servicio servicio) throws Exception {
        log.info("=== BUSCANDO CITAS POR SERVICIO: {} ===", servicio);

        // OBTENER TODAS LAS CITAS Y FILTRAR POR SERVICIO
        List<Cita> todasLasCitas = citaRepo.findAll();
        List<Cita> citasFiltradas = todasLasCitas.stream()
                .filter(cita -> cita.getListaServicios() != null &&
                        cita.getListaServicios().contains(servicio))
                .collect(Collectors.toList());

        log.info("Citas encontradas: {}", citasFiltradas.size());
        return convertirCitasADTO(citasFiltradas);
    }

    public List<Cita> obtenerCitasProximosDosDias() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime dentroDeDosDias = ahora.plusDays(2);
        return citaRepo.findCitasEntreFechas(ahora, dentroDeDosDias);
    }

    /**
     * Simula el envío del recordatorio
     */
    public void enviarRecordatorio(Cita cita) throws Exception {
        emailService.sendMail(
                new EmailDTO("Rcordatorio cita de: " + cita.getUsuario().getNombre(),
                        "El usuario " + cita.getUsuario().getNombre() +
                                " tiene un cita pendiente registrada para el día " + cita.getFechaCita() +
                                " La información del la cita es:  " + cita.getDescripcionCita(),cita.getUsuario().getEmail())
        );}
}