package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.DTO.FacturaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Servicio;
import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import co.edu.uniquindio.BarakaLashes.servicio.Implementaciones.FacturaServicio;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/facturacion")
@RequiredArgsConstructor
public class FacturacionController {

    private final FacturaServicio facturaServicio;
    private final CitaServicio citaServicio;

    /**
     * Muestra la pasarela de pagos para una cita
     */
    @GetMapping("/pagar/{idCita}")
    public String mostrarPasarelaPago(@PathVariable Integer idCita,
                                      Model model,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        try {
            log.info("=== MOSTRANDO PASARELA DE PAGO PARA CITA {} ===", idCita);

            String usuarioEmail = (String) session.getAttribute("email");
            if (usuarioEmail == null) {
                redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para realizar el pago");
                return "redirect:/auth/login";
            }

            // Obtener información de la cita
            CitaDTO cita = citaServicio.obtenerCita(idCita);

            // Verificar que la cita pertenece al usuario
            if (!cita.getEmailCliente().equals(usuarioEmail)) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para pagar esta cita");
                return "redirect:/citas/historial";
            }

            // Calcular total
            double subtotal = 0.0;
            for (String servicioStr : cita.getServicios()) {
                try {
                    Servicio servicio = Servicio.valueOf(servicioStr);
                    subtotal += facturaServicio.obtenerPrecioServicio(servicio);
                } catch (IllegalArgumentException e) {
                    log.warn("Servicio no reconocido: {}", servicioStr);
                }
            }
            double iva = subtotal * 0.19;
            double total = subtotal + iva;

            model.addAttribute("cita", cita);
            model.addAttribute("subtotal", subtotal);
            model.addAttribute("iva", iva);
            model.addAttribute("total", total);

            return "pasarelaPago";

        } catch (Exception e) {
            log.error("Error al mostrar pasarela de pago: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al cargar la pasarela de pago: " + e.getMessage());
            return "redirect:/citas/historial";
        }
    }

    /**
     * Procesa el pago y genera la factura
     */
    @PostMapping("/procesar/{idCita}")
    public String procesarPago(@PathVariable Integer idCita,
                               @RequestParam String metodoPago,
                               @RequestParam(required = false) String numeroTarjeta,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            log.info("=== PROCESANDO PAGO PARA CITA {} ===", idCita);

            String usuarioEmail = (String) session.getAttribute("email");
            if (usuarioEmail == null) {
                throw new Exception("Usuario no autenticado");
            }

            // Verificar permisos
            CitaDTO cita = citaServicio.obtenerCita(idCita);
            if (!cita.getEmailCliente().equals(usuarioEmail)) {
                throw new Exception("No tienes permiso para pagar esta cita");
            }

            // Simular procesamiento de pago (agregar delay para realismo)
            Thread.sleep(1500);

            // Crear la factura
            FacturaDTO factura = facturaServicio.crearFacturaDesdeIdCita(idCita);

            log.info("PAGO PROCESADO EXITOSAMENTE - Factura ID: {}", factura.getIdFactura());

            // Redirigir a la página de factura
            return "redirect:/facturacion/factura/" + factura.getIdFactura();

        } catch (Exception e) {
            log.error("Error al procesar pago: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al procesar el pago: " + e.getMessage());
            return "redirect:/facturacion/pagar/" + idCita;
        }
    }

    /**
     * Muestra la factura generada
     */
    @GetMapping("/factura/{idFactura}")
    public String mostrarFactura(@PathVariable Integer idFactura,
                                 Model model,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        try {
            log.info("=== MOSTRANDO FACTURA {} ===", idFactura);

            String usuarioEmail = (String) session.getAttribute("email");
            if (usuarioEmail == null) {
                redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión");
                return "redirect:/auth/login";
            }

            FacturaDTO factura = facturaServicio.obtenerFactura(idFactura);

            if (!factura.getEmailUsuario().equals(usuarioEmail)) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para ver esta factura");
                return "redirect:/citas/historial";
            }

            // 🔹 Mapeamos servicio → precio
            Map<String, Double> precios = new HashMap<>();
            for (Servicio servicio : factura.getServicios()) {
                precios.put(servicio.name(), facturaServicio.obtenerPrecioServicio(servicio));
            }

            model.addAttribute("factura", factura);
            model.addAttribute("precios", precios); // agregamos al modelo

            return "facturaDetalle";

        } catch (Exception e) {
            log.error("Error al mostrar factura: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al cargar la factura: " + e.getMessage());
            return "redirect:/citas/historial";
        }
    }


    /**
     * Muestra el historial de facturas del usuario
     */
    @GetMapping("/historial")
    public String historialFacturas(Model model, HttpSession session) {
        String emailUsuario = (String) session.getAttribute("email");

        if (emailUsuario == null) {
            model.addAttribute("error", "No se encontró un usuario asociado a la sesión");
            model.addAttribute("facturas", Collections.emptyList());
            return "historialFacturas";
        }

        try {
            List<FacturaDTO> facturas = facturaServicio.listarFacturasPorUsuarioEmail(emailUsuario);
            model.addAttribute("facturas", facturas);
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar las facturas: " + e.getMessage());
            model.addAttribute("facturas", Collections.emptyList());
        }

        return "historialFacturas";
    }
}