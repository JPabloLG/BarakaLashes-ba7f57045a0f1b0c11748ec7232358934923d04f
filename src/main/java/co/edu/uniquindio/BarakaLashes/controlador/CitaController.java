package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.Cita.CitaCalendarioDTO;
import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaServicio citaServicio;


    @GetMapping("/nueva")
    public String mostrarFormularioCita(Model model) {
        model.addAttribute("cita", new CitaDTO());
        return "crearCita";
    }

    // NUEVO: Método para mostrar el catálogo de servicios
    @GetMapping("/catalogo")
    public String mostrarCatalogoServicios() {
        return "catalogoServicios";
    }

    // AGREGAR este método al CitaController existente:

    @PostMapping("/nueva")
    public String crearCita(@ModelAttribute("cita") CitaDTO citaDTO,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        try {
            int idCita = citaServicio.crearCita(citaDTO);
            redirectAttributes.addFlashAttribute("mensaje", "Cita creada exitosamente con ID: " + idCita);

            // Guardar email en sesión para mostrar historial correctamente
            if (citaDTO.getEmailCliente() != null) {
                session.setAttribute("email", citaDTO.getEmailCliente());
            }

            // NUEVA LÍNEA: Redirigir a la pasarela de pago en lugar del historial
            return "redirect:/facturacion/pagar/" + idCita;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la cita: " + e.getMessage());
            return "redirect:/citas/nueva?error";
        }
    }

    @GetMapping
    public String listarCitas(Model model, HttpSession session) {
        try {
            String usuarioEmail = (String) session.getAttribute("usuarioEmail");
            log.info("=== LISTANDO CITAS PARA USUARIO: {} ===", usuarioEmail);

            if (usuarioEmail == null) {
                log.error("Usuario no autenticado");
                model.addAttribute("error", "Debes iniciar sesión para ver tus citas");
                return "listaCitas";
            }

            List<CitaDTO> citas = citaServicio.listarCitasPorUsuarioEmail(usuarioEmail);

            log.info("Citas encontradas: {}", citas.size());
            model.addAttribute("citas", citas);
            return "listaCitas";
        } catch (Exception e) {
            log.error("Error al listar citas: {}", e.getMessage());
            model.addAttribute("error", "Error al cargar las citas: " + e.getMessage());
            return "listaCitas";
        }
    }

    @GetMapping("/{idCita}")
    public String obtenerCita(@PathVariable int idCita, Model model) {
        try {
            CitaDTO cita = citaServicio.obtenerCita(idCita);
            model.addAttribute("cita", cita);
            return "detalleCita";
        } catch (Exception e) {
            model.addAttribute("error", "Error al obtener cita: " + e.getMessage());
            return "redirect:/citas";
        }
    }

    @GetMapping("/{idCita}/editar")
    public String mostrarFormularioEdicion(@PathVariable int idCita, Model model) {
        try {
            CitaDTO cita = citaServicio.obtenerCita(idCita);
            model.addAttribute("cita", cita);
            return "editarCita";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar cita para edición: " + e.getMessage());
            return "redirect:/citas";
        }
    }

    @PostMapping("/{idCita}/editar")
    public String actualizarCita(@PathVariable int idCita,
                                 @ModelAttribute("cita") CitaDTO citaDTO,
                                 RedirectAttributes redirectAttributes) {
        try {
            int idActualizado = citaServicio.actualizarCita(idCita, citaDTO);
            redirectAttributes.addFlashAttribute("mensaje", "Cita actualizada exitosamente");
            return "redirect:/citas/" + idActualizado;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar cita: " + e.getMessage());
            return "redirect:/citas/" + idCita + "/editar?error";
        }
    }

    // SOLO UN método cancelarCita - ELIMINA EL DUPLICADO
    @PostMapping("/{idCita}/cancelar")
    public String cancelarCita(@PathVariable int idCita,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            String usuarioEmail = (String) session.getAttribute("usuarioEmail");
            log.info("=== CANCELANDO CITA {} PARA USUARIO: {} ===", idCita, usuarioEmail);

            if (usuarioEmail == null) {
                throw new Exception("Usuario no autenticado");
            }

            // Verificar que la cita pertenece al usuario
            CitaDTO cita = citaServicio.obtenerCita(idCita);
            if (!cita.getEmailCliente().equals(usuarioEmail)) {
                throw new Exception("No tienes permiso para cancelar esta cita");
            }

            // Actualizar estado a CANCELADA
            cita.setEstadoCita(EstadoCita.CANCELADA);
            citaServicio.actualizarCita(idCita, cita);

            log.info("CITA CANCELADA EXITOSAMENTE");
            redirectAttributes.addFlashAttribute("success", "Cita cancelada exitosamente");

        } catch (Exception e) {
            log.error(" ERROR AL CANCELAR CITA: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al cancelar cita: " + e.getMessage());
        }

        return "redirect:/citas/historial"; // Redirige al historial
    }

    @GetMapping("/usuario/{idUsuario}")
    public String listarCitasPorUsuario(@PathVariable int idUsuario, Model model) {
        try {
            List<CitaDTO> citas = citaServicio.listarCitasPorUsuario(idUsuario);
            model.addAttribute("citas", citas);
            model.addAttribute("titulo", "Citas del Usuario " + idUsuario);
            return "listaCitas";
        } catch (Exception e) {
            model.addAttribute("error", "Error al listar citas del usuario: " + e.getMessage());
            return "listaCitas";
        }
    }

    @GetMapping("/historial")
    public String historialCitas(Model model, HttpSession session) {
        // 1. Obtener el email del cliente de la sesión (establecido en /citas/nueva)
        String emailUsuario = (String) session.getAttribute("email");

        if (emailUsuario == null) {
            // Si no hay email, mostramos la vista con un error, pero no fallamos.
            model.addAttribute("error", "No se encontró un cliente asociado a la sesión. Por favor, inicie sesión o cree una cita.");
            model.addAttribute("citas", Collections.emptyList());
            return "historial";
        }

        try {
            // 2. Usar el email de la sesión para buscar las citas.
            List<CitaDTO> citas = citaServicio.listarCitasPorUsuarioEmail(emailUsuario);
            model.addAttribute("citas", citas);
        } catch (Exception e) {
            // 3. Si falla el servicio, capturamos la excepción y la mostramos.
            model.addAttribute("error", "Error al cargar las citas: " + e.getMessage());
            model.addAttribute("citas", Collections.emptyList());
        }
        return "historial";
    }

    @GetMapping("/rango")
    @ResponseBody
    public ResponseEntity<?> obtenerCitasPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer idUsuario,
            @RequestParam(required = false) Integer idEmpleado) {

        try {
            log.info("Solicitando citas por rango: {} a {}, usuario: {}, empleado: {}",
                    fechaInicio, fechaFin, idUsuario, idEmpleado);

            List<CitaDTO> citas = citaServicio.obtenerCitasPorRango(fechaInicio, fechaFin, idUsuario, idEmpleado);
            return ResponseEntity.ok(citas);

        } catch (Exception e) {
            log.error("Error al obtener citas por rango: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Error al obtener citas: " + e.getMessage()));
        }
    }


    @GetMapping("/calendario")
    @ResponseBody
    public ResponseEntity<?> obtenerCitasCalendario(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer idUsuario,
            @RequestParam(required = false) Integer idEmpleado) {

        try {
            List<CitaCalendarioDTO> citas = citaServicio.obtenerCitasParaCalendario(
                    fechaInicio, fechaFin, idUsuario, idEmpleado);
            return ResponseEntity.ok(citas);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Error al obtener citas para calendario: " + e.getMessage()));
        }
    }

    @GetMapping("/calendario/vista")
    public String mostrarVistaCalendario(Model model) {
        try {
            LocalDate hoy = LocalDate.now();
            model.addAttribute("fechaInicio", hoy.withDayOfMonth(1));
            model.addAttribute("fechaFin", hoy.withDayOfMonth(hoy.lengthOfMonth()));

            return "calendario";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el calendario");
            return "calendario";
        }
    }

    @GetMapping("/disponibilidad")
    @ResponseBody
    public ResponseEntity<?> obtenerHorariosDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam Integer idEmpleado) {

        try {
            List<LocalDateTime> horarios = citaServicio.obtenerHorariosDisponibles(fecha, idEmpleado);
            return ResponseEntity.ok(horarios);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Error al obtener horarios disponibles: " + e.getMessage()));
        }
    }

    @GetMapping("/recordatorio")
    public String mostrarRecordatorio(){
        return "recordatorio";
    }

}