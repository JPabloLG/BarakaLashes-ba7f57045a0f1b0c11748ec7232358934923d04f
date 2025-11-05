package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.modelo.Empleado;
import co.edu.uniquindio.BarakaLashes.repositorio.EmpleadoRepositorio;
import co.edu.uniquindio.BarakaLashes.servicio.EmpleadoServicio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("barakaLashes-app")
@CrossOrigin(value = "http://localhost:8080")
public class EmpleadoControlador {

    private static final Logger logger =
            LoggerFactory.getLogger(EmpleadoControlador.class);

    @Autowired
    private EmpleadoServicio empleadoServicio;
    @Autowired
    private EmpleadoRepositorio empleadoRepositorio;
    
    @GetMapping ("/empleados")
    public List<Empleado> obtenerEmpleados(){
        var empleados = empleadoServicio.listarEmpleados();
        empleados.forEach((empleado ->  logger.info(empleado.toString())));
        return empleados;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empleado> obtenerEmpleado(@PathVariable Integer id) {
        try {
            Empleado empleado = empleadoServicio.buscarEmpleadoPorId(id);
            return ResponseEntity.ok(empleado);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/empleados/{id}")
    public void eliminarEmpleado(Integer id) throws Exception {
        if (!empleadoRepositorio.existsById(id)) {
            throw new Exception("El empleado con id " + id + " no existe");
        }
        empleadoRepositorio.deleteById(id);
    }
}
