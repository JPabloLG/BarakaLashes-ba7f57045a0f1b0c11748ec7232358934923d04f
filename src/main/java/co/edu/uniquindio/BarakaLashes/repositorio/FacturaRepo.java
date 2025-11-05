package co.edu.uniquindio.BarakaLashes.repositorio;

import co.edu.uniquindio.BarakaLashes.modelo.Factura;
import co.edu.uniquindio.BarakaLashes.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacturaRepo extends JpaRepository<Factura, Integer> {

    /**
     * Encuentra todas las facturas de un usuario ordenadas por fecha descendente
     */
    List<Factura> findByUsuarioOrderByFechaDesc(Usuario usuario);

    /**
     * Encuentra facturas por ID de usuario
     */
    List<Factura> findByUsuarioIdUsuario(Integer idUsuario);

    /**
     * Encuentra facturas por email del usuario
     */
    List<Factura> findByUsuarioEmailOrderByFechaDesc(String email);
}