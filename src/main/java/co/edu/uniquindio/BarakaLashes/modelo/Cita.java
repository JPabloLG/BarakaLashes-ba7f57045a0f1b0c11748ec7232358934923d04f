package co.edu.uniquindio.BarakaLashes.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "Cita")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCita;

    @Column(nullable = false, length = 100)
    private String nombreCita;

    @Column(length = 255)
    private String descripcionCita;

    @Column(nullable = false)
    private LocalDateTime fechaCita;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCita estadoCita;

    @ElementCollection(targetClass = Servicio.class, fetch = FetchType.EAGER)
    @CollectionTable(
            name = "serviciosCita",
            joinColumns = @JoinColumn(name = "idCita")
    )
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "servicio")
    private Set<Servicio> listaServicios;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "negocio_id")
    private Negocio negocio;
}
