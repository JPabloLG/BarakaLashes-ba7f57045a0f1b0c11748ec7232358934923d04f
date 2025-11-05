package co.edu.uniquindio.BarakaLashes.repositorio;

import co.edu.uniquindio.BarakaLashes.modelo.Cita;
import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepositorio extends JpaRepository<Cita, Integer> {

    // Buscar citas por ID del usuario
    List<Cita> findByUsuarioIdUsuario(Integer idUsuario);

    // Buscar citas por email del usuario (a través de la relación)
    List<Cita> findByUsuarioEmail(String email);

    // Buscar citas por email del usuario ordenadas por fecha descendente
    List<Cita> findByUsuarioEmailOrderByFechaCitaDesc(String email);

    // Buscar citas por estado
    List<Cita> findByEstadoCita(EstadoCita estado);

    // Buscar citas por usuario y estado
    List<Cita> findByUsuarioEmailAndEstadoCita(String email, EstadoCita estado);

    // Query personalizada para buscar citas cancelables
    @Query("SELECT c FROM Cita c WHERE c.idCita = :idCita AND " +
            "(c.estadoCita = 'PENDIENTE' OR c.estadoCita = 'CONFIRMADA')")
    Optional<Cita> findCancelableCita(@Param("idCita") Integer idCita);

    // Actualizar estado de una cita
    @Modifying
    @Query("UPDATE Cita c SET c.estadoCita = :estado WHERE c.idCita = :idCita")
    void actualizarEstadoCita(@Param("idCita") Integer idCita, @Param("estado") EstadoCita estado);

    // Query para obtener todas las citas
    @Query("SELECT c FROM Cita c LEFT JOIN FETCH c.usuario LEFT JOIN FETCH c.listaServicios")
    List<Cita> obtenerTodasLasCitasConRelaciones();


    // Consultas básicas por rango de fechas
    List<Cita> findByFechaCitaBetween(LocalDateTime inicio, LocalDateTime fin);

    List<Cita> findByFechaCitaBetweenAndUsuarioIdUsuario(LocalDateTime inicio, LocalDateTime fin, Integer usuarioId);

    List<Cita> findByFechaCitaBetweenAndNegocioIdNegocio(LocalDateTime inicio, LocalDateTime fin, Integer negocioId);

    List<Cita> findByFechaCitaBetweenAndUsuarioIdUsuarioAndNegocioIdNegocio(
            LocalDateTime inicio, LocalDateTime fin, Integer usuarioId, Integer negocioId);


    @Query("SELECT c FROM Cita c " +
            "LEFT JOIN FETCH c.usuario u " +
            "LEFT JOIN FETCH c.negocio n " +
            "WHERE c.fechaCita BETWEEN :inicio AND :fin " +
            "AND (:usuarioId IS NULL OR u.idUsuario = :usuarioId) " +
            "AND (:negocioId IS NULL OR n.idNegocio = :negocioId) " +
            "ORDER BY c.fechaCita ASC")
    List<Cita> findCitasCompletasPorRango(@Param("inicio") LocalDateTime inicio,
                                          @Param("fin") LocalDateTime fin,
                                          @Param("usuarioId") Integer usuarioId,
                                          @Param("negocioId") Integer negocioId);

    // Consultas por estado y rango de fechas
    List<Cita> findByEstadoCitaAndFechaCitaBetween(EstadoCita estado, LocalDateTime inicio, LocalDateTime fin);

    List<Cita> findByEstadoCitaAndFechaCitaBetweenAndNegocioIdNegocio(
            EstadoCita estado, LocalDateTime inicio, LocalDateTime fin, Integer negocioId);


    // Consulta para verificar disponibilidad de horario
    @Query("SELECT c FROM Cita c WHERE c.fechaCita = :fechaHora " +
            "AND c.negocio.idNegocio = :negocioId " +
            "AND c.estadoCita != 'CANCELADA'")
    Optional<Cita> findCitaEnHorario(@Param("fechaHora") LocalDateTime fechaHora,
                                     @Param("negocioId") Integer negocioId);

    //  Consulta para citas del día de un empleado específico
    @Query("SELECT c FROM Cita c WHERE DATE(c.fechaCita) = CURRENT_DATE " +
            "AND c.negocio.idNegocio = :negocioId " +
            "AND c.estadoCita != 'CANCELADA' " +
            "ORDER BY c.fechaCita ASC")
    List<Cita> findCitasHoyPorNegocio(@Param("negocioId") Integer negocioId);

    //  Consulta para estadísticas/resumen
    @Query("SELECT COUNT(c) FROM Cita c WHERE " +
            "c.fechaCita BETWEEN :inicio AND :fin " +
            "AND (:negocioId IS NULL OR c.negocio.idNegocio = :negocioId) " +
            "AND c.estadoCita = :estado")
    Long countCitasPorEstadoYRango(@Param("inicio") LocalDateTime inicio,
                                   @Param("fin") LocalDateTime fin,
                                   @Param("negocioId") Integer negocioId,
                                   @Param("estado") EstadoCita estado);

    // Consulta para próximas citas (próximos 7 días)
    @Query("SELECT c FROM Cita c WHERE " +
            "c.fechaCita BETWEEN CURRENT_TIMESTAMP AND :fin " +
            "AND c.estadoCita IN ('PENDIENTE', 'CONFIRMADA') " +
            "AND (:usuarioId IS NULL OR c.usuario.idUsuario = :usuarioId) " +
            "ORDER BY c.fechaCita ASC")
    List<Cita> findProximasCitas(@Param("fin") LocalDateTime fin,
                                 @Param("usuarioId") Integer usuarioId);

    // Consulta para citas vencidas (fechas pasadas que no están completadas o canceladas)
    @Query("SELECT c FROM Cita c WHERE " +
            "c.fechaCita < CURRENT_TIMESTAMP " +
            "AND c.estadoCita IN ('PENDIENTE', 'CONFIRMADA') " +
            "AND (:negocioId IS NULL OR c.negocio.idNegocio = :negocioId)")
    List<Cita> findCitasVencidas(@Param("negocioId") Integer negocioId);

    @Query("SELECT c FROM Cita c WHERE c.fechaCita BETWEEN :inicio AND :fin")
    List<Cita> findCitasEntreFechas(@Param("inicio") LocalDateTime inicio,
                                    @Param("fin") LocalDateTime fin);
}
