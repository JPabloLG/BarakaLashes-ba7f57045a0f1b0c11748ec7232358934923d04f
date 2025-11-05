package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.Cita.ResumenCitasDTO;
import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import co.edu.uniquindio.BarakaLashes.servicio.UsuarioServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador encargado de gestionar las vistas y operaciones relacionadas con las citas
 * del empleado dentro del sistema BarakaLashes.
 */
@Controller
@RequestMapping("/empleado")
@RequiredArgsConstructor
public class EmpleadoCitaController {

    private final CitaServicio citaServicio;
    private final UsuarioServicio usuarioServicio;

    /**
     * Muestra el panel principal (dashboard) del empleado con información general
     * sobre las citas del día y de la semana.
     *
     * @param model Modelo para pasar los datos a la vista.
     * @return Vista del dashboard del empleado.
     */
    @GetMapping("/dashboard")
    public String dashboardEmpleado(Model model) {
        try {
            LocalDate hoy = LocalDate.now();

            // Resumen de citas del día actual
            ResumenCitasDTO resumenHoy = citaServicio.obtenerResumenCitas(hoy, hoy, null);
            model.addAttribute("resumenHoy", resumenHoy);

            // Citas programadas para hoy
            List<CitaDTO> citasHoy = citaServicio.obtenerCitasPorRango(hoy, hoy, null, null);
            model.addAttribute("citasHoy", citasHoy);

            // Citas de la próxima semana
            List<CitaDTO> citasSemana = citaServicio.obtenerCitasPorRango(
                    hoy, hoy.plusDays(7), null, null);
            model.addAttribute("citasSemana", citasSemana);

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
        }

        return "empleado/dashboard";
    }

    /**
     * Muestra el calendario de citas del empleado para el mes actual.
     *
     * @param model Modelo para pasar datos a la vista.
     * @return Vista del calendario del empleado.
     */
    @GetMapping("/calendario")
    public String calendarioEmpleado(Model model) {
        LocalDate hoy = LocalDate.now();
        model.addAttribute("fechaInicio", hoy.withDayOfMonth(1));
        model.addAttribute("fechaFin", hoy.withDayOfMonth(hoy.lengthOfMonth()));

        try {
            model.addAttribute("clientes", usuarioServicio.listarUsuarios());
        } catch (Exception e) {
            System.out.println("No se pudieron cargar los clientes: " + e.getMessage());
        }

        return "empleado/calendario";
    }

    /**
     * Lista todas las citas según los filtros opcionales de fecha, estado y cliente.
     *
     * @param fechaInicio Fecha inicial del rango de búsqueda (opcional).
     * @param fechaFin    Fecha final del rango de búsqueda (opcional).
     * @param estado      Estado de la cita (opcional).
     * @param clienteId   ID del cliente (opcional).
     * @param model       Modelo para pasar los datos a la vista.
     * @return Vista con la lista de citas filtradas.
     */
    @GetMapping("/citas")
    public String listarTodasLasCitas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer clienteId,
            Model model) {

        try {
            // Rango de fechas por defecto (últimos 30 días hasta 30 días futuros)
            if (fechaInicio == null) fechaInicio = LocalDate.now().minusDays(30);
            if (fechaFin == null) fechaFin = LocalDate.now().plusDays(30);

            // Obtener citas dentro del rango
            List<CitaDTO> citas = citaServicio.obtenerCitasPorRango(fechaInicio, fechaFin, clienteId, null);

            // Filtrar por estado si se especifica
            if (estado != null && !estado.isEmpty() && !estado.equalsIgnoreCase("TODOS")) {
                EstadoCita estadoCita = EstadoCita.valueOf(estado.toUpperCase());
                citas = citas.stream()
                        .filter(c -> c.getEstadoCita() == estadoCita)
                        .collect(Collectors.toList());
            }

            // Cálculo de estadísticas
            long total = citas.size();
            long confirmadas = citas.stream().filter(c -> c.getEstadoCita() == EstadoCita.CONFIRMADA).count();
            long pendientes = citas.stream().filter(c -> c.getEstadoCita() == EstadoCita.PENDIENTE).count();
            long canceladas = citas.stream().filter(c -> c.getEstadoCita() == EstadoCita.CANCELADA).count();

            // Agregar datos al modelo
            model.addAttribute("citas", citas);
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
            model.addAttribute("estadoSeleccionado", estado);
            model.addAttribute("clienteSeleccionado", clienteId);
            model.addAttribute("totalCitas", total);
            model.addAttribute("confirmadas", confirmadas);
            model.addAttribute("pendientes", pendientes);
            model.addAttribute("canceladas", canceladas);
            model.addAttribute("estados", EstadoCita.values());

            try {
                model.addAttribute("clientes", usuarioServicio.listarUsuarios());
            } catch (Exception e) {
                System.out.println("No se pudieron cargar los clientes: " + e.getMessage());
            }

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar citas: " + e.getMessage());
            model.addAttribute("citas", Collections.emptyList());
            model.addAttribute("totalCitas", 0);
            model.addAttribute("confirmadas", 0);
            model.addAttribute("pendientes", 0);
            model.addAttribute("canceladas", 0);
        }

        // Retornar la vista correcta
        return "empleado/listaCitas";
    }

    /**
     * Muestra el detalle de una cita específica.
     *
     * @param idCita Identificador de la cita.
     * @param model  Modelo para pasar los datos a la vista.
     * @return Vista con el detalle de la cita.
     */
    @GetMapping("/citas/{idCita}")
    public String verDetalleCita(@PathVariable int idCita, Model model) {
        try {
            CitaDTO cita = citaServicio.obtenerCita(idCita);
            model.addAttribute("cita", cita);
            return "empleado/detalleCita";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar cita: " + e.getMessage());
            return "redirect:/empleado/citas";
        }
    }

    /**
     * Muestra el formulario para editar una cita específica.
     *
     * @param idCita Identificador de la cita a editar.
     * @param model  Modelo para pasar los datos a la vista.
     * @return Vista del formulario de edición de cita.
     */
    @GetMapping("/citas/{idCita}/editar")
    public String editarCita(@PathVariable int idCita, Model model) {
        try {
            CitaDTO cita = citaServicio.obtenerCita(idCita);
            model.addAttribute("cita", cita);
            return "empleado/editarCita";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar cita para editar: " + e.getMessage());
            return "redirect:/empleado/citas";
        }
    }

    /**
     * Cancela una cita existente.
     *
     * @param idCita             Identificador de la cita a cancelar.
     * @param redirectAttributes Atributos para mostrar mensajes de éxito o error.
     * @return Redirección a la lista de citas.
     */
    @PostMapping("/citas/{idCita}/cancelar")
    public String cancelarCita(@PathVariable int idCita, RedirectAttributes redirectAttributes) {
        try {
            citaServicio.cancelarCita(idCita);
            redirectAttributes.addFlashAttribute("success", "Cita cancelada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar cita: " + e.getMessage());
        }
        return "redirect:/empleado/citas";
    }

    /**
     * Devuelve la clase CSS correspondiente al estado de una cita.
     *
     * @param estado Estado de la cita.
     * @return Clase CSS asociada al estado.
     */
    @ModelAttribute("getEstadoBadgeClass")
    public String getEstadoBadgeClass(EstadoCita estado) {
        if (estado == null) return "badge-secondary";
        return switch (estado) {
            case CONFIRMADA -> "badge-success";
            case PENDIENTE -> "badge-warning";
            case CANCELADA -> "badge-danger";
            default -> "badge-secondary";
        };
    }

    /**
     * Determina si una cita está cancelada.
     *
     * @param estado Estado de la cita.
     * @return true si la cita está cancelada, false en caso contrario.
     */
    @ModelAttribute("isCancelada")
    public boolean isCancelada(EstadoCita estado) {
        return estado != null && estado == EstadoCita.CANCELADA;
    }
}
