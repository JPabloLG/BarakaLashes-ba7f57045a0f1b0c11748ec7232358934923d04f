/**
 * Pruebas unitarias para {@link CitaController}.
 *
 * Esta clase verifica el correcto funcionamiento de los endpoints del controlador
 * de citas, evaluando tanto el flujo principal como casos excepcionales.
 *
 * <p><b>Escenarios cubiertos:</b></p>
 * <ul>
 *     <li>Visualización del formulario para crear una cita nueva.</li>
 *     <li>Creación exitosa de una cita (con redirección al historial).</li>
 *     <li>Listado de citas de un usuario autenticado.</li>
 *     <li>Obtención del detalle de una cita existente.</li>
 *     <li>Cancelación de cita (casos exitoso, sin sesión y no autorizado).</li>
 *     <li>Consulta de citas por rango de fechas en formato JSON.</li>
 * </ul>
 *
 * <p><b>Detalles adicionales:</b></p>
 * <ul>
 *     <li>Se utiliza {@code MockMvc} en modo {@code standaloneSetup} para evitar la resolución
 *     de plantillas Thymeleaf reales durante las pruebas.</li>
 *     <li>El servicio {@link CitaServicio} es simulado mediante {@code @MockBean}.</li>
 *     <li>Se emplea {@link ArgumentCaptor} para verificar el estado de la cita al ser cancelada.</li>
 * </ul>
 *
 * <p><b>Autor:</b> Helen Giraldo, Juan Esteban Maya, Juan Pablo López, Willinton Vergara<br>
 * <b>Fecha:</b> 07/10/2025</p>
 */

package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Clase de prueba que valida los distintos endpoints del controlador {@link CitaController}.
 * <p>
 * Las pruebas emplean {@code MockMvc} para simular peticiones HTTP y verificar respuestas,
 * sin requerir un servidor real ni vistas Thymeleaf.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CitaControllerTest {

    /** MockMvc para simular las solicitudes HTTP hacia el controlador. */
    private MockMvc mockMvc;

    /** Servicio de citas simulado mediante Mockito. */
    @MockBean
    private CitaServicio citaServicio;

    /** Controlador bajo prueba, inyectado automáticamente. */
    @Autowired
    private CitaController citaController;

    /** Objeto de cita utilizado en múltiples pruebas. */
    private CitaDTO cita;

    /**
     * Inicializa los objetos necesarios antes de cada prueba.
     * <ul>
     *     <li>Configura el {@code MockMvc} en modo standalone.</li>
     *     <li>Inicializa un {@link CitaDTO} base con datos de ejemplo.</li>
     * </ul>
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(citaController).build();

        cita = new CitaDTO();
        cita.setIdCita(1);
        cita.setNombreCita("Extensiones de pestañas");
        cita.setEmailCliente("cliente@correo.com");
        cita.setEstadoCita(EstadoCita.CONFIRMADA);
        cita.setFechaCita(LocalDateTime.now());
    }

    /** Verifica que se muestre correctamente el formulario de nueva cita. */
    @Test
    @DisplayName("Debería mostrar el formulario para crear una nueva cita")
    void testMostrarFormularioNuevaCita() throws Exception {
        mockMvc.perform(get("/citas/nueva"))
                .andExpect(status().isOk())
                .andExpect(view().name("crearCita"))
                .andExpect(model().attributeExists("cita"));
    }

    /** Verifica la creación exitosa de una cita y la redirección al historial. */
    @Test
    @DisplayName("Debería crear una nueva cita exitosamente y redirigir al historial")
    void testCrearCitaExito() throws Exception {
        when(citaServicio.crearCita(any(CitaDTO.class))).thenReturn(1);

        mockMvc.perform(post("/citas/nueva")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("nombreCita", "Cita Prueba")
                        .param("emailCliente", "cliente@correo.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/citas/historial"));
    }

    /** Verifica el listado de citas de un usuario autenticado. */
    @Test
    @DisplayName("Debería listar las citas del usuario autenticado")
    void testListarCitasUsuario() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuarioEmail", "cliente@correo.com");

        when(citaServicio.listarCitasPorUsuarioEmail("cliente@correo.com"))
                .thenReturn(List.of(cita));

        mockMvc.perform(get("/citas").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("listaCitas"))
                .andExpect(model().attributeExists("citas"));
    }

    /** Verifica la obtención del detalle de una cita existente. */
    @Test
    @DisplayName("Debería obtener el detalle de una cita existente")
    void testObtenerDetalleCita() throws Exception {
        when(citaServicio.obtenerCita(1)).thenReturn(cita);

        mockMvc.perform(get("/citas/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("detalleCita"))
                .andExpect(model().attributeExists("cita"));
    }

    /**
     * Verifica la cancelación exitosa de una cita.
     * <ul>
     *     <li>Debe invocar {@link CitaServicio actualizarCita(Integer, CitaDTO)}.</li>
     *     <li>El estado de la cita debe cambiar a {@code CANCELADA}.</li>
     * </ul>
     */
    @Test
    @DisplayName("Debería cancelar una cita correctamente (llama al servicio y marca CANCELADA)")
    void testCancelarCita_Success() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuarioEmail", "cliente@correo.com");

        when(citaServicio.obtenerCita(1)).thenReturn(cita);
        when(citaServicio.actualizarCita(eq(1), any(CitaDTO.class))).thenReturn(1);

        mockMvc.perform(post("/citas/1/cancelar").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/citas/historial"));

        ArgumentCaptor<CitaDTO> captor = ArgumentCaptor.forClass(CitaDTO.class);
        verify(citaServicio, times(1)).actualizarCita(eq(1), captor.capture());
        CitaDTO dtoPasado = captor.getValue();
        assertThat(dtoPasado.getEstadoCita()).isEqualTo(EstadoCita.CANCELADA);
    }

    /**
     * Verifica que si un usuario no autenticado intenta cancelar una cita,
     * se redirige con un mensaje de error y no se invoca el servicio.
     */
    @Test
    @DisplayName("Cancelar cita sin sesión: debe redirigir e incluir flash error")
    void testCancelarCita_SinSesion() throws Exception {
        when(citaServicio.obtenerCita(anyInt())).thenReturn(cita);

        mockMvc.perform(post("/citas/1/cancelar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/citas/historial"))
                .andExpect(flash().attributeExists("error"));

        verify(citaServicio, never()).actualizarCita(anyInt(), any(CitaDTO.class));
    }

    /**
     * Verifica que si un usuario intenta cancelar una cita que no le pertenece,
     * se redirige con error y no se invoca el método de actualización.
     */
    @Test
    @DisplayName("Cancelar cita por usuario distinto: debe fallar y añadir flash error")
    void testCancelarCita_NoPropietario() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuarioEmail", "otro@correo.com");

        when(citaServicio.obtenerCita(1)).thenReturn(cita);

        mockMvc.perform(post("/citas/1/cancelar").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/citas/historial"))
                .andExpect(flash().attributeExists("error"));

        verify(citaServicio, never()).actualizarCita(anyInt(), any(CitaDTO.class));
    }

    /** Verifica que se devuelvan correctamente las citas en un rango de fechas en formato JSON. */
    @Test
    @DisplayName("Debería devolver citas dentro de un rango de fechas como JSON")
    void testObtenerCitasPorRango() throws Exception {
        when(citaServicio.obtenerCitasPorRango(any(), any(), any(), any()))
                .thenReturn(List.of(cita));

        mockMvc.perform(get("/citas/rango")
                        .param("fechaInicio", "2025-10-01")
                        .param("fechaFin", "2025-10-30"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
