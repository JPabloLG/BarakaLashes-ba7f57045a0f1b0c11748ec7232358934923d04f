package co.edu.uniquindio.BarakaLashes.DTO;

import co.edu.uniquindio.BarakaLashes.modelo.Servicio;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDTO {

    private Integer idFactura;
    private Integer idUsuario;
    private String nombreUsuario;
    private String emailUsuario;
    private List<String> nombresEmpleados;
    private List<Servicio> servicios;
    private double subtotal;
    private double iva;
    private double total;
    private LocalDate fecha;
    private Integer idCita;

    // Campos calculados para la vista
    public String getFechaFormateada() {
        if (fecha != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return fecha.format(formatter);
        }
        return "";
    }

    public String getTotalFormateado() {
        return String.format("$%.2f", total);
    }

    public String getSubtotalFormateado() {
        return String.format("$%.2f", subtotal);
    }

    public String getIvaFormateado() {
        return String.format("$%.2f", iva);
    }
}