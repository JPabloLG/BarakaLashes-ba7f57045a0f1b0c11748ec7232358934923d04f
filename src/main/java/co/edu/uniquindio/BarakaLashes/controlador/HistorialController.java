package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.mappers.CitaMapper;
import co.edu.uniquindio.BarakaLashes.modelo.Cita;
import co.edu.uniquindio.BarakaLashes.modelo.Servicio;
import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Controlador encargado de gestionar el historial, cancelación y modificación de citas
 * para un cliente autenticado. Utiliza {@link CitaServicio} para la lógica de negocio
 * y {@link CitaMapper} para la conversión entre entidades y DTOs.
 */
@Slf4j
@Controller
@RequestMapping("/historial")
@RequiredArgsConstructor
public class HistorialController {

    private final CitaServicio citaServicio;
    private final CitaMapper citaMapper;

    /**
     * Muestra el historial de citas del usuario autenticado.
     */
    @GetMapping
    public String mostrarHistorial(Model model, HttpSession session) {
        try {
            // Obtener email del usuario autenticado
            String email = obtenerEmailSesion(session);
            if (email == null) {
                log.warn("Usuario no autenticado intentando acceder al historial");
                return "redirect:/auth/login?error=no_autenticado";
            }

            log.info("=== ACCESO A HISTORIAL - Usuario: {} ===", email);

            // Obtener citas desde el servicio
            List<CitaDTO> citas = citaServicio.obtenerHistorialCitas(email);


            model.addAttribute("citas", citas);
            model.addAttribute("titulo", "Mis Citas");

            return "historial";

        } catch (Exception e) {
            log.error("Error al cargar historial: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al cargar el historial: " + e.getMessage());
            model.addAttribute("citas", List.of());
            model.addAttribute("titulo", "Mis Citas");
            return "historial";
        }
    }

    /**
     * Cancela una cita del usuario autenticado.
     */
    @PostMapping("/cancelar/{id}")
    public String cancelarCita(@PathVariable Integer id,
                               RedirectAttributes redirectAttributes,
                               HttpSession session) {
        try {
            String email = obtenerEmailSesion(session);
            if (email == null) {
                redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para cancelar una cita");
                return "redirect:/auth/login";
            }

            log.info("=== CANCELANDO CITA {} para usuario: {} ===", id, email);

            // Obtener cita para verificar propiedad
            CitaDTO cita = citaServicio.obtenerCita(id);
            if (!cita.getEmailCliente().equals(email)) {
                throw new Exception("No tienes permiso para cancelar esta cita");
            }

            citaServicio.cancelarCita(id);
            redirectAttributes.addFlashAttribute("success", "Cita cancelada exitosamente");
            log.info("Cita {} cancelada con éxito", id);

        } catch (Exception e) {
            log.error("Error al cancelar cita {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "No se pudo cancelar la cita: " + e.getMessage());
        }

        return "redirect:/historial";
    }

    /**
     * Muestra el formulario para modificar una cita existente.
     */
    @GetMapping("/modificar/{id}")
    public String mostrarModificarCita(@PathVariable Integer id,
                                       Model model,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        try {
            String email = obtenerEmailSesion(session);
            if (email == null) {
                return "redirect:/auth/login?error=no_autenticado";
            }

            log.info("=== ACCESO A MODIFICAR CITA {} ===", id);

            CitaDTO cita = citaServicio.obtenerCita(id);

            if (!cita.getEmailCliente().equals(email)) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para modificar esta cita");
                return "redirect:/historial";
            }

            if (!cita.isModificable()) {
                redirectAttributes.addFlashAttribute("error",
                        "No puedes modificar una cita que está " + cita.getEstadoCita().name().toLowerCase());
                return "redirect:/historial";
            }

            model.addAttribute("cita", cita);
            return "modificarCita";

        } catch (Exception e) {
            log.error("Error al cargar modificarCita: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al cargar la cita: " + e.getMessage());
            return "redirect:/historial";
        }
    }

    /**
     * Procesa la modificación de una cita existente.
     */
    @PostMapping("/modificar/{id}")
    public String procesarModificarCita(@PathVariable Integer id,
                                        @RequestParam String nombreCita,
                                        @RequestParam String fechaCita,
                                        @RequestParam(required = false) String descripcionCita,
                                        @RequestParam(required = false) Set<Servicio> serviciosSeleccionados,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        try {
            String email = obtenerEmailSesion(session);
            if (email == null) {
                return "redirect:/auth/login?error=no_autenticado";
            }

            log.info("=== PROCESANDO MODIFICACIÓN DE CITA {} ===", id);

            CitaDTO citaExistente = citaServicio.obtenerCita(id);
            if (!citaExistente.getEmailCliente().equals(email)) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para modificar esta cita");
                return "redirect:/historial";
            }

            // Crear DTO actualizado
            CitaDTO citaActualizada = new CitaDTO();
            citaActualizada.setNombreCita(nombreCita);
            citaActualizada.setDescripcionCita(descripcionCita);
            citaActualizada.setFechaCita(LocalDateTime.parse(fechaCita));
            citaActualizada.setServiciosSeleccionados(serviciosSeleccionados);
            citaActualizada.setEstadoCita(citaExistente.getEstadoCita());
            citaActualizada.setEmailCliente(email);

            citaServicio.actualizarCita(id, citaActualizada);
            redirectAttributes.addFlashAttribute("success", "Cita modificada exitosamente");
            log.info("Cita {} modificada con éxito", id);

        } catch (Exception e) {
            log.error("Error al modificar cita {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "No se pudo modificar la cita: " + e.getMessage());
        }

        return "redirect:/historial";
    }

    /**
     * Obtiene el email del usuario autenticado desde la sesión.
     */
    private String obtenerEmailSesion(HttpSession session) {
        String email = (String) session.getAttribute("usuarioEmail");
        if (email == null || email.isEmpty()) {
            email = (String) session.getAttribute("email");
        }
        return (email != null && !email.isEmpty()) ? email : null;
    }
}
