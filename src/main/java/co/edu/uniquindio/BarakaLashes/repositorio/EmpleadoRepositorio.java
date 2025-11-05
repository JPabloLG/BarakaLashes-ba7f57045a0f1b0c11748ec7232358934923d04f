package co.edu.uniquindio.BarakaLashes.repositorio;

import co.edu.uniquindio.BarakaLashes.modelo.Empleado;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EmpleadoRepositorio extends JpaRepository<Empleado, Integer> {

}
