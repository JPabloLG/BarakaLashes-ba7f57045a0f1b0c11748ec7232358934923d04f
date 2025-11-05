package co.edu.uniquindio.BarakaLashes.mappers;

import co.edu.uniquindio.BarakaLashes.DTO.RegistroDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Usuario;

public class UsuarioMapper {

    public static Usuario toEntity(RegistroDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCedula(dto.getCedula());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setPassword(dto.getPassword());
        return usuario;
    }
}