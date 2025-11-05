package co.edu.uniquindio.BarakaLashes.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "factura")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"empleados", "servicios"})
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFactura;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @ManyToMany
    @JoinTable(
            name = "facturaEmpleado",
            joinColumns = @JoinColumn(name = "idFactura"),
            inverseJoinColumns = @JoinColumn(name = "idEmpleado")
    )
    private List<Empleado> empleados;

    @ElementCollection(targetClass = Servicio.class)
    @CollectionTable(
            name = "facturaServicios",
            joinColumns = @JoinColumn(name = "idFactura")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "servicio")
    private List<Servicio> servicios;

    @Column(nullable = false)
    private double total;

    @Column(nullable = false)
    private LocalDate fecha;
}
