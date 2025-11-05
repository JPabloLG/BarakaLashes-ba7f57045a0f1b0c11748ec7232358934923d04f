package co.edu.uniquindio.BarakaLashes.servicio.Implementaciones;

import co.edu.uniquindio.BarakaLashes.DTO.FacturaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.*;
import co.edu.uniquindio.BarakaLashes.repositorio.CitaRepositorio;
import co.edu.uniquindio.BarakaLashes.repositorio.FacturaRepo;
import co.edu.uniquindio.BarakaLashes.repositorio.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacturaServicio {

    private final FacturaRepo facturaRepo;
    private final UsuarioRepositorio usuarioRepo;
    private final CitaRepositorio citaRepo;

    // Precios de los servicios
    private static final Map<Servicio, Double> PRECIOS_SERVICIOS = new HashMap<>();
    static {
        PRECIOS_SERVICIOS.put(Servicio.CLASICA, 80000.0);
        PRECIOS_SERVICIOS.put(Servicio.VOLUMEN, 120000.0);
        PRECIOS_SERVICIOS.put(Servicio.CEJAS, 45000.0);
        PRECIOS_SERVICIOS.put(Servicio.TINTE, 35000.0);
        PRECIOS_SERVICIOS.put(Servicio.PERMANENTE, 55000.0);
    }

    private static final double IVA_PORCENTAJE = 0.19;

    /**
     * Crea una factura a partir de una cita y ACTUALIZA el estado a CONFIRMADO
     */
    @Transactional
    public FacturaDTO crearFacturaDesdeIdCita(Integer idCita) throws Exception {
        log.info("Creando factura desde cita ID: {}", idCita);

        Cita cita = citaRepo.findById(idCita)
                .orElseThrow(() -> new Exception("Cita no encontrada"));

        // ✅ ACTUALIZAR ESTADO DE LA CITA A CONFIRMADO
        cita.setEstadoCita(EstadoCita.CONFIRMADA);
        citaRepo.save(cita);
        log.info("Estado de cita actualizado a CONFIRMADO para cita ID: {}", idCita);

        Usuario usuario = cita.getUsuario();
        Set<Servicio> serviciosSet = cita.getListaServicios();
        List<Servicio> servicios = new ArrayList<>(serviciosSet);

        // Calcular totales
        double subtotal = calcularSubtotal(servicios);
        double iva = subtotal * IVA_PORCENTAJE;
        double total = subtotal + iva;

        // Crear entidad Factura
        Factura factura = new Factura();
        factura.setUsuario(usuario);
        factura.setServicios(servicios);
        factura.setTotal(total);
        factura.setFecha(LocalDate.now());

        // ⚠️ Empleado por defecto (simulado)
        factura.setEmpleados(Collections.emptyList()); // no se guarda en BD, solo por consistencia

        Factura facturaSaved = facturaRepo.save(factura);
        log.info("Factura creada con ID: {}", facturaSaved.getIdFactura());

        return convertirADTO(facturaSaved, subtotal, iva, idCita);
    }

    /**
     * Obtiene una factura por su ID
     */
    public FacturaDTO obtenerFactura(Integer idFactura) throws Exception {
        Factura factura = facturaRepo.findById(idFactura)
                .orElseThrow(() -> new Exception("Factura no encontrada"));

        double subtotal = calcularSubtotal(factura.getServicios());
        double iva = subtotal * IVA_PORCENTAJE;

        return convertirADTO(factura, subtotal, iva, null);
    }

    /**
     * Lista todas las facturas de un usuario por email
     */
    public List<FacturaDTO> listarFacturasPorUsuarioEmail(String email) throws Exception {
        Usuario usuario = usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        List<Factura> facturas = facturaRepo.findByUsuarioOrderByFechaDesc(usuario);

        return facturas.stream()
                .map(f -> {
                    double subtotal = calcularSubtotal(f.getServicios());
                    double iva = subtotal * IVA_PORCENTAJE;
                    return convertirADTO(f, subtotal, iva, null);
                })
                .collect(Collectors.toList());
    }

    /**
     * Calcula el subtotal de una lista de servicios
     */
    private double calcularSubtotal(List<Servicio> servicios) {
        return servicios.stream()
                .mapToDouble(s -> PRECIOS_SERVICIOS.getOrDefault(s, 0.0))
                .sum();
    }

    /**
     * Convierte una entidad Factura a DTO
     */
    private FacturaDTO convertirADTO(Factura factura, double subtotal, double iva, Integer idCita) {
        FacturaDTO dto = new FacturaDTO();
        dto.setIdFactura(factura.getIdFactura());
        dto.setIdUsuario(factura.getUsuario().getIdUsuario());
        dto.setNombreUsuario(factura.getUsuario().getNombre());
        dto.setEmailUsuario(factura.getUsuario().getEmail());

        // ✅ Si no hay empleados, asignar uno por defecto
        if (factura.getEmpleados() == null || factura.getEmpleados().isEmpty()) {
            dto.setNombresEmpleados(Collections.singletonList("Empleado 1"));
        } else {
            dto.setNombresEmpleados(
                    factura.getEmpleados().stream()
                            .map(Empleado::getNombre)
                            .collect(Collectors.toList())
            );
        }

        dto.setServicios(factura.getServicios());
        dto.setSubtotal(subtotal);
        dto.setIva(iva);
        dto.setTotal(factura.getTotal());
        dto.setFecha(factura.getFecha());
        dto.setIdCita(idCita);

        return dto;
    }

    /**
     * Obtiene el precio de un servicio
     */
    public double obtenerPrecioServicio(Servicio servicio) {
        return PRECIOS_SERVICIOS.getOrDefault(servicio, 0.0);
    }
}