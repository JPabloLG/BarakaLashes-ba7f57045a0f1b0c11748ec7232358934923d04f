package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.Cita.CitaActualizadaDTO;
import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que gestiona las operaciones CRUD relacionadas con las citas.
 * <p>
 * Este controlador expone una API para crear, consultar, actualizar, modificar parcialmente,
 * eliminar y listar citas del sistema. Se comunica directamente con el servicio {@link CitaServicio}
 * para ejecutar la lógica de negocio.
 * </p>
 *
 * <h3>Características principales:</h3>
 * <ul>
 *     <li>Implementa operaciones CRUD completas (Create, Read, Update, Delete).</li>
 *     <li>Soporta actualizaciones parciales mediante el método HTTP PATCH.</li>
 *     <li>Devuelve respuestas estandarizadas con {@link ResponseEntity} y códigos HTTP apropiados.</li>
 *     <li>Maneja errores de negocio devolviendo estados adecuados (400, 404, 500, etc.).</li>
 * </ul>
 *
 * <p>
 * Prefijo común de la API: <b>/api/citas</b>
 * </p>
 *
 + @author Helen Giraldo, Juan Esteban Maya Sanchez, Juan Pablo López, Willinton Vergara
 * @version 1.0
 * @since 2025-10-07
 */
@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaAPIController {

    /**
     * Servicio encargado de la lógica de negocio relacionada con la gestión de citas.
     */
    private final CitaServicio citaServicio;

    // ============================================================
    // MÉTODO 1: CREAR NUEVA CITA
    // ============================================================

    /**
     * Crea una nueva cita en el sistema.
     *
     * @param citaDTO Objeto {@link CitaDTO} con la información de la cita a registrar.
     * @return {@link ResponseEntity} con el ID generado de la cita (código 201 si se crea correctamente)
     *         o un código 400 en caso de error de validación o lógica.
     */
    @PostMapping
    public ResponseEntity<Integer> crearCita(@RequestBody CitaDTO citaDTO) {
        try {
            int idCita = citaServicio.crearCita(citaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(idCita);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(-1);
        }
    }

    // ============================================================
    // MÉTODO 2: OBTENER UNA CITA POR ID
    // ============================================================

    /**
     * Obtiene la información detallada de una cita según su ID.
     *
     * @param idCita Identificador único de la cita a consultar.
     * @return {@link ResponseEntity} con el objeto {@link CitaDTO} si se encuentra la cita,
     *         o un código 404 si no existe.
     */
    @GetMapping("/{idCita}")
    public ResponseEntity<CitaDTO> obtenerCita(@PathVariable int idCita) {
        try {
            CitaDTO cita = citaServicio.obtenerCita(idCita);
            return ResponseEntity.ok(cita);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ============================================================
    // MÉTODO 3: ACTUALIZAR UNA CITA (PUT)
    // ============================================================

    /**
     * Actualiza completamente la información de una cita existente.
     *
     * @param idCita  Identificador de la cita a actualizar.
     * @param citaDTO Objeto {@link CitaDTO} con los nuevos valores de la cita.
     * @return {@link ResponseEntity} con el ID actualizado si la operación fue exitosa (200 OK),
     *         o un código 400 si ocurrió un error de validación o lógica.
     */
    @PutMapping("/{idCita}")
    public ResponseEntity<Integer> actualizarCita(@PathVariable int idCita, @RequestBody CitaDTO citaDTO) {
        try {
            int idActualizado = citaServicio.actualizarCita(idCita, citaDTO);
            return ResponseEntity.ok(idActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(-1);
        }
    }

    // ============================================================
    // MÉTODO 4: MODIFICAR PARCIALMENTE UNA CITA (PATCH)
    // ============================================================

    /**
     * Permite modificar parcialmente algunos datos de una cita (como fecha, hora o servicio)
     * sin necesidad de reemplazar toda la entidad.
     *
     * @param idCita               Identificador de la cita a modificar.
     * @param citaActualizadaDTO   Objeto {@link CitaActualizadaDTO} con los campos que deben actualizarse.
     * @return {@link ResponseEntity} con un mensaje de éxito si la cita fue actualizada,
     *         o un mensaje de error con código 400 si algo falla.
     */
    @PatchMapping("/{idCita}")
    public ResponseEntity<?> modificarCita(
            @PathVariable int idCita,
            @RequestBody CitaActualizadaDTO citaActualizadaDTO) {

        try {
            int idActualizada = citaServicio.modificarCita(idCita, citaActualizadaDTO);
            return ResponseEntity.ok("Cita actualizada correctamente con ID: " + idActualizada);

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Error al actualizar la cita: " + e.getMessage());
        }
    }

    // ============================================================
    // MÉTODO 5: ELIMINAR UNA CITA
    // ============================================================

    /**
     * Elimina una cita existente del sistema.
     *
     * @param idCita Identificador de la cita a eliminar.
     * @return {@link ResponseEntity} con el ID de la cita eliminada si se encuentra (200 OK),
     *         o un código 404 si no existe la cita.
     */
    @DeleteMapping("/{idCita}")
    public ResponseEntity<Integer> eliminarCita(@PathVariable int idCita) {
        try {
            int idEliminado = citaServicio.eliminarCita(idCita);
            return ResponseEntity.ok(idEliminado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(-1);
        }
    }

    // ============================================================
    // MÉTODO 6: LISTAR TODAS LAS CITAS
    // ============================================================

    /**
     * Lista todas las citas registradas en el sistema.
     *
     * @return {@link ResponseEntity} con una lista de {@link CitaDTO} y código 200 si la consulta es exitosa,
     *         o un código 500 si ocurre un error interno del servidor.
     */
    @GetMapping
    public ResponseEntity<List<CitaDTO>> listarCitas() {
        try {
            List<CitaDTO> citas = citaServicio.listarCitas();
            return ResponseEntity.ok(citas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
