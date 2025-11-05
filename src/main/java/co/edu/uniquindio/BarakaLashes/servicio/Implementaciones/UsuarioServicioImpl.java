package co.edu.uniquindio.BarakaLashes.servicio.Implementaciones;

import co.edu.uniquindio.BarakaLashes.DTO.RegistroDTO;
import co.edu.uniquindio.BarakaLashes.mappers.UsuarioMapper;
import co.edu.uniquindio.BarakaLashes.modelo.RolUsuario;
import co.edu.uniquindio.BarakaLashes.modelo.Usuario;
import co.edu.uniquindio.BarakaLashes.repositorio.UsuarioRepositorio;
import co.edu.uniquindio.BarakaLashes.servicio.UsuarioServicio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioServicioImpl implements UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;

    @Override
    public Usuario registrar(RegistroDTO dto) {
        // Validaciones
        if (usuarioRepositorio.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (usuarioRepositorio.existsByCedula(dto.getCedula())) {
            throw new RuntimeException("La cédula ya está registrada");
        }
        if (!dto.getPassword().equals(dto.getConfirmarPassword())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        // Usamos mapper
        Usuario usuario = UsuarioMapper.toEntity(dto);

        // Determinar rol
        usuario.setRol(dto.getCedula().equals("1000000000") ? RolUsuario.ADMIN : RolUsuario.USUARIO);

        // Guardar en BD
        usuarioRepositorio.save(usuario);

        log.info("Usuario registrado con éxito: {}", usuario.getEmail());

        return usuario;
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepositorio.findAll();
    }

    @Override
    public Usuario obtenerUsuarioPorId(Integer id) {
        return usuarioRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Override
    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepositorio.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }

    @Override
    public Usuario obtenerUsuarioPorCedula(String cedula) {
        return usuarioRepositorio.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con cédula: " + cedula));
    }

    @Override
    public Usuario actualizarUsuario(Integer id, Usuario usuarioActualizado) {
        Usuario usuarioExistente = obtenerUsuarioPorId(id);

        // Validar que el nuevo email no esté en uso por otro usuario
        if (!usuarioExistente.getEmail().equals(usuarioActualizado.getEmail()) &&
                usuarioRepositorio.existsByEmail(usuarioActualizado.getEmail())) {
            throw new RuntimeException("El email " + usuarioActualizado.getEmail() + " ya está en uso por otro usuario");
        }

        // Validar que la nueva cédula no esté en uso por otro usuario
        if (!usuarioExistente.getCedula().equals(usuarioActualizado.getCedula()) &&
                usuarioRepositorio.existsByCedula(usuarioActualizado.getCedula())) {
            throw new RuntimeException("La cédula " + usuarioActualizado.getCedula() + " ya está en uso por otro usuario");
        }

        // Actualizar campos
        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setApellido(usuarioActualizado.getApellido());
        usuarioExistente.setCedula(usuarioActualizado.getCedula());
        usuarioExistente.setEmail(usuarioActualizado.getEmail());
        usuarioExistente.setTelefono(usuarioActualizado.getTelefono());
        usuarioExistente.setNegocio(usuarioActualizado.getNegocio());

        return usuarioRepositorio.save(usuarioExistente);
    }

    @Override
    public void eliminarUsuario(Integer id) {
        if (!usuarioRepositorio.existsById(id)) {
            throw new RuntimeException("Usuario a eliminar no encontrado con ID: " + id);
        }
        usuarioRepositorio.deleteById(id);
    }

    @Override
    public boolean existeUsuarioPorEmail(String email) {
        return usuarioRepositorio.existsByEmail(email);
    }

    @Override
    public boolean existeUsuarioPorCedula(String cedula) {
        return usuarioRepositorio.existsByCedula(cedula);
    }
}