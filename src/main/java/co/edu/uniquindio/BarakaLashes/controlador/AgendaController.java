package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
import co.edu.uniquindio.BarakaLashes.modelo.Servicio;
import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Controlador responsable de gestionar la visualización y filtrado de la agenda de citas.
 * <p>
 * Este controlador permite al usuario (administrador o empleado) consultar el listado de citas
 * en un rango de fechas determinado, aplicar filtros por cliente, estado o servicio,
 * y visualizar estadísticas resumidas de las citas (pendientes, confirmadas, totales).
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *     <li>Mostrar la agenda de citas filtrada por fecha, cliente, estado o servicio.</li>
 *     <li>Generar los datos en formato JSON para representación visual en el calendario.</li>
 *     <li>Calcular estadísticas de las citas (pendientes, confirmadas, totales).</li>
 *     <li>Controlar errores en el proceso de carga de agenda.</li>
 * </ul>
 *
 * <p>
 * Vista asociada: <b>agendaCitas.html</b>
 * </p>
 *
 * @author Helen Giraldo, Juan Esteban Maya Sanchez, Juan Pablo López, Willinton Vergara
 * @version 1.0
 * @since 2025-10-07
 */
@Slf4j
@Controller
@RequestMapping("/agenda")
@RequiredArgsConstructor
public class AgendaController {

    /**
     * Servicio encargado de gestionar la lógica de negocio relacionada con las citas.
     */
    private final CitaServicio citaServicio;

    /**
     * Muestra la agenda de citas con posibilidad de aplicar filtros por fecha, cliente,
     * estado o servicio. Si no se envían parámetros de filtrado, se muestra el mes actual por defecto.
     *
     * <p>El método utiliza los siguientes pasos:</p>
     * <ol>
     *     <li>Establece el rango de fechas (por defecto, el mes actual si no se pasa ningún parámetro).</li>
     *     <li>Aplica el filtro correspondiente (cliente, servicio o estado).</li>
     *     <li>Obtiene las citas desde {@link CitaServicio}.</li>
     *     <li>Calcula estadísticas (total, pendientes, confirmadas).</li>
     *     <li>Convierte la lista de citas a JSON para el calendario del frontend.</li>
     *     <li>Retorna la vista <b>"agendaCitas"</b> con los atributos cargados.</li>
     * </ol>
     *
     * @param fechaInicio Fecha de inicio del rango de búsqueda (opcional, formato: yyyy-MM-dd).
     * @param fechaFin    Fecha final del rango de búsqueda (opcional, formato: yyyy-MM-dd).
     * @param cliente     Nombre o correo del cliente a filtrar (opcional).
     * @param estado      Estado de la cita a filtrar (opcional: PENDIENTE, CONFIRMADA, CANCELADA...).
     * @param servicio    Tipo de servicio (opcional, valor enumerado de {@link Servicio}).
     * @param model       Objeto {@link Model} para pasar atributos a la vista.
     *
     * @return La vista <b>"agendaCitas"</b> con la información de citas, filtros y estadísticas cargadas.
     */
    @GetMapping
    public String mostrarAgenda(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String servicio,
            Model model) {

        try {
            log.info("=== ACCESO A AGENDA ===");

            // Establecer rango de fechas por defecto (mes actual)
            LocalDateTime inicio = fechaInicio != null
                    ? LocalDate.parse(fechaInicio).atStartOfDay()
                    : LocalDate.now().withDayOfMonth(1).atStartOfDay();

            LocalDateTime fin = fechaFin != null
                    ? LocalDate.parse(fechaFin).atTime(LocalTime.MAX)
                    : LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(LocalTime.MAX);

            log.info("Rango de fechas: {} - {}", inicio, fin);

            // Obtener citas según filtros aplicados
            List<CitaDTO> citas;

            if (cliente != null && !cliente.isEmpty()) {
                // Filtro por cliente
                citas = citaServicio.buscarCitasPorCliente(cliente);

            } else if (servicio != null && !servicio.isEmpty()) {
                // Filtro por servicio
                citas = citaServicio.buscarCitasPorServicio(Servicio.valueOf(servicio));

            } else if (estado != null && !estado.isEmpty()) {
                // Filtro por estado de cita
                citas = citaServicio.obtenerCitasConFiltros(inicio, fin, null, EstadoCita.valueOf(estado));

            } else {
                // Sin filtros: obtener todas las citas del rango
                citas = citaServicio.obtenerCitasPorRangoFechas(inicio, fin);
            }

            log.info("Citas encontradas: {}", citas.size());

            // Calcular estadísticas
            long totalCitas = citas.size();
            long citasPendientes = citas.stream().filter(c -> c.getEstadoCita() == EstadoCita.PENDIENTE).count();
            long citasConfirmadas = citas.stream().filter(c -> c.getEstadoCita() == EstadoCita.CONFIRMADA).count();

            // Conversión de lista a JSON para uso en el calendario frontend
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules(); // Para manejo correcto de LocalDateTime
            String citasJson = mapper.writeValueAsString(citas);

            // Cargar datos al modelo
            model.addAttribute("citas", citas);
            model.addAttribute("citasJson", citasJson);
            model.addAttribute("totalCitas", totalCitas);
            model.addAttribute("citasPendientes", citasPendientes);
            model.addAttribute("citasConfirmadas", citasConfirmadas);
            model.addAttribute("fechaInicio", fechaInicio != null ? fechaInicio : inicio.toLocalDate().toString());
            model.addAttribute("fechaFin", fechaFin != null ? fechaFin : fin.toLocalDate().toString());
            model.addAttribute("cliente", cliente);
            model.addAttribute("estadoSeleccionado", estado);
            model.addAttribute("servicioSeleccionado", servicio);

            // Retornar la vista principal de agenda
            return "agendaCitas";

        } catch (Exception e) {
            log.error("Error al cargar agenda: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al cargar la agenda: " + e.getMessage());
            model.addAttribute("citas", List.of());
            return "agendaCitas";
        }
    }
}
