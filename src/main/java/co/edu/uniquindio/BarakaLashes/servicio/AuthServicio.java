package co.edu.uniquindio.BarakaLashes.servicio;

import co.edu.uniquindio.BarakaLashes.DTO.LoginDTO;
import co.edu.uniquindio.BarakaLashes.DTO.RegistroDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Usuario;

public interface AuthServicio {
    Usuario login(LoginDTO loginDTO);
    Usuario registrar(RegistroDTO registroDTO);
    boolean validarCredenciales(String email, String password);
    boolean existeUsuario(String email);
    boolean existeCedula(String cedula);
    Usuario obtenerUsuarioPorEmail(String email);
}