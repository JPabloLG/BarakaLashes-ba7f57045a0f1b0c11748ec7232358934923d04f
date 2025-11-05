package co.edu.uniquindio.BarakaLashes.servicio;

import co.edu.uniquindio.BarakaLashes.DTO.RegistroDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Usuario;
import java.util.List;

public interface UsuarioServicio {
    Usuario registrar(RegistroDTO dto);
    List<Usuario> listarUsuarios(); // ← Cambiado el nombre
    Usuario obtenerUsuarioPorId(Integer id);
    Usuario obtenerUsuarioPorEmail(String email);
    Usuario obtenerUsuarioPorCedula(String cedula);
    Usuario actualizarUsuario(Integer id, Usuario usuarioActualizado);
    void eliminarUsuario(Integer id);
    boolean existeUsuarioPorEmail(String email);
    boolean existeUsuarioPorCedula(String cedula);
}