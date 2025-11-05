package co.edu.uniquindio.BarakaLashes.servicio;

import co.edu.uniquindio.BarakaLashes.DTO.Cita.CitaActualizadaDTO;
import co.edu.uniquindio.BarakaLashes.DTO.Cita.CitaCalendarioDTO;
import co.edu.uniquindio.BarakaLashes.DTO.Cita.ResumenCitasDTO;
import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
import co.edu.uniquindio.BarakaLashes.modelo.Cita;
import co.edu.uniquindio.BarakaLashes.modelo.Servicio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CitaServicio {

    /**
     * Crea una nueva cita
     * @param citaDTO datos de la cita a crear
     * @return ID de la cita creada
     * @throws Exception si hay algún error en la creación
     */
    int crearCita(CitaDTO citaDTO) throws Exception;

    /**
     * Actualiza una cita existente
     * @param idCita ID de la cita a actualizar
     * @param citaDTO nuevos datos de la cita
     * @return ID de la cita actualizada
     * @throws Exception si la cita no existe o hay error en la actualización
     */
    int actualizarCita(int idCita, CitaDTO citaDTO) throws Exception;

    /**
     * Elimina una cita
     * @param idCita ID de la cita a eliminar
     * @return ID de la cita eliminada
     * @throws Exception si la cita no existe
     */
    int eliminarCita(int idCita) throws Exception;


    int modificarCita(int idCita, CitaActualizadaDTO citaDTO) throws Exception;
    /**
     * Obtiene una cita por su ID
     * @param idCita ID de la cita
     * @return DTO con los datos de la cita
     * @throws Exception si la cita no existe
     */
    CitaDTO obtenerCita(int idCita) throws Exception;

    /**
     * Lista todas las citas del sistema
     * @return Lista de todas las citas
     */
    List<CitaDTO> listarCitas();

    /**
     * Lista las citas de un usuario específico por su ID
     * @param idUsuario ID del usuario
     * @return Lista de citas del usuario
     */
    List<CitaDTO> listarCitasPorUsuario(int idUsuario);

    /**
     * Lista las citas de un usuario por su email
     * @param emailUsuario Email del usuario
     * @return Lista de citas del usuario
     */
    List<CitaDTO> listarCitasPorUsuarioEmail(String emailUsuario);

    /**
     * Cancela una cita
     * @param idCita ID de la cita a cancelar
     * @return ID de la cita cancelada
     * @throws Exception si la cita no se puede cancelar
     */
    int cancelarCita(int idCita) throws Exception;

    /**
     * Obtiene el historial completo de citas de un usuario
     * @param emailUsuario Email del usuario
     * @return Lista completa de citas del usuario (todas los estados)
     * @throws Exception si el usuario no existe
     */
    List<CitaDTO> obtenerHistorialCitas(String emailUsuario) throws Exception;





    /**
     * Consulta citas por rango de fechas con filtros opcionales
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @param idUsuario Filtro opcional por ID de usuario
     * @param idEmpleado Filtro opcional por ID de empleado/negocio
     * @return Lista de citas en el rango especificado
     * @throws Exception si hay error en la consulta
     */
    List<CitaDTO> obtenerCitasPorRango(LocalDate fechaInicio, LocalDate fechaFin,
                                       Integer idUsuario, Integer idEmpleado) throws Exception;

    /**
     * Obtiene citas formateadas para calendario
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param idUsuario Filtro por usuario
     * @param idEmpleado Filtro por empleado
     * @return Lista de citas en formato calendario
     * @throws Exception si hay error en la consulta
     */
    List<CitaCalendarioDTO> obtenerCitasParaCalendario(LocalDate fechaInicio, LocalDate fechaFin,
                                                       Integer idUsuario, Integer idEmpleado) throws Exception;

    /**
     * Obtiene horarios disponibles para una fecha y empleado específicos
     * @param fecha Fecha a consultar
     * @param idEmpleado ID del empleado/negocio
     * @return Lista de horarios disponibles
     * @throws Exception si hay error en la consulta
     */
    List<LocalDateTime> obtenerHorariosDisponibles(LocalDate fecha, Integer idEmpleado) throws Exception;

    /**
     * Obtiene resumen estadístico de citas
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param idEmpleado Filtro por empleado
     * @return Resumen con estadísticas de citas
     * @throws Exception si hay error en la consulta
     */
    ResumenCitasDTO obtenerResumenCitas(LocalDate fechaInicio, LocalDate fechaFin,
                                        Integer idEmpleado) throws Exception;




    List<CitaDTO> obtenerCitasPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws Exception;

    /**
     * Obtiene citas con múltiples filtros
     */
    List<CitaDTO> obtenerCitasConFiltros(LocalDateTime fechaInicio, LocalDateTime fechaFin,
                                         String emailCliente, EstadoCita estado) throws Exception;

    /**
     * Busca citas por cliente (email)
     */
    List<CitaDTO> buscarCitasPorCliente(String busqueda) throws Exception;

    /**
     * Busca citas por servicio
     */
    List<CitaDTO> buscarCitasPorServicio(Servicio servicio) throws Exception;
    List<Cita> obtenerCitasProximosDosDias();
    void enviarRecordatorio(Cita cita) throws Exception;
}





