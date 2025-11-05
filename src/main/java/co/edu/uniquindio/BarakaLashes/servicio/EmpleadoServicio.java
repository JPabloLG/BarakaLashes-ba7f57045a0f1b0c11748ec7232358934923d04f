package co.edu.uniquindio.BarakaLashes.servicio;

import co.edu.uniquindio.BarakaLashes.modelo.Empleado;

import java.util.List;

public interface EmpleadoServicio {

     List<Empleado> listarEmpleados();

     Empleado buscarEmpleadoPorId(Integer idEmpleado);

     Empleado guardarEmpleado(Empleado empleado);

     void eliminarEmpleado(Empleado empleado);
}
