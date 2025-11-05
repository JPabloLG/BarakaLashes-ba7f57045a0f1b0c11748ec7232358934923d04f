/*
package co.edu.uniquindio.BarakaLashes.servicio.Implementaciones;

import co.edu.uniquindio.BarakaLashes.DTO.RegistroDTO;
import co.edu.uniquindio.BarakaLashes.modelo.RolUsuario;
import co.edu.uniquindio.BarakaLashes.modelo.Usuario;
import co.edu.uniquindio.BarakaLashes.repositorio.UsuarioRepositorio;
import co.edu.uniquindio.BarakaLashes.servicio.AuthServicio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServicioImpl implements AuthServicio {

    private final UsuarioRepositorio usuarioRepositorio;

    // Cédulas que serán ADMIN
    private static final String[] ADMIN_CEDULAS = {
            "1000000000"  // Solo esta cédula crea ADMIN
    };

    @Override
    @Transactional
    public Usuario registrar(RegistroDTO registroDTO) {
        log.info("=== INICIANDO REGISTRO ===");
        log.info("Email: {}, Cédula: {}", registroDTO.getEmail(), registroDTO.getCedula());
        log.info("Password recibido: '{}'", registroDTO.getPassword());
        log.info("Confirmar Password: '{}'", registroDTO.getConfirmarPassword());

        // Validaciones
        if (usuarioRepositorio.existsByEmail(registroDTO.getEmail())) {
            log.error("Email ya registrado: {}", registroDTO.getEmail());
            throw new RuntimeException("El email ya está registrado");
        }

        if (usuarioRepositorio.existsByCedula(registroDTO.getCedula())) {
            log.error("Cédula ya registrada: {}", registroDTO.getCedula());
            throw new RuntimeException("La cédula ya está registrada");
        }

        if (!registroDTO.getPassword().equals(registroDTO.getConfirmarPassword())) {
            log.error("Contraseñas no coinciden");
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        if (registroDTO.getPassword().length() < 6) {
            log.error("Contraseña muy corta: {}", registroDTO.getPassword().length());
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
        }

        // Determinar rol
        RolUsuario rol = esAdmin(registroDTO.getCedula()) ? RolUsuario.ADMIN : RolUsuario.USUARIO;
        log.info("Rol asignado: {}", rol);

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(registroDTO.getNombre());
        usuario.setApellido(registroDTO.getApellido());
        usuario.setCedula(registroDTO.getCedula());
        usuario.setEmail(registroDTO.getEmail());
        usuario.setTelefono(registroDTO.getTelefono());

        // Encriptar contraseña - VERIFICAR QUE NO SEA NULL
        String passwordPlain = registroDTO.getPassword();
        log.info("Password a encriptar: '{}'", passwordPlain);

        if (passwordPlain == null || passwordPlain.trim().isEmpty()) {
            log.error("ERROR: Password es null o vacío");
            throw new RuntimeException("La contraseña no puede estar vacía");
        }


        usuario.setPassword(passwordPlain);
        usuario.setRol(rol);

        log.info("Guardando usuario en BD...");
        Usuario usuarioGuardado = usuarioRepositorio.save(usuario);
        log.info(" Usuario guardado con ID: {}", usuarioGuardado.getIdUsuario());

        // Verificar que se guardó correctamente
        log.info(" Usuario registrado: {} {}", usuarioGuardado.getNombre(), usuarioGuardado.getApellido());
        log.info(" Email: {}", usuarioGuardado.getEmail());
        log.info("Rol: {}", usuarioGuardado.getRol());

        return usuarioGuardado;
    }

    @Override
    public boolean validarCredenciales(String email, String password) {
        return usuarioRepositorio.findByEmail(email)
                .map(usuario -> password.equals(usuario.getPassword()))
                .orElse(false);
    }

    @Override
    public boolean existeUsuario(String email) {
        return usuarioRepositorio.existsByEmail(email);
    }

    @Override
    public boolean existeCedula(String cedula) {
        return usuarioRepositorio.existsByCedula(cedula);
    }

    // Método privado para verificar si es admin
    private boolean esAdmin(String cedula) {
        for (String adminCedula : ADMIN_CEDULAS) {
            if (adminCedula.equals(cedula)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Usuario login(co.edu.uniquindio.BarakaLashes.DTO.LoginDTO loginDTO) {
        // Este método ya no se usa directamente, Spring Security maneja el login
        throw new UnsupportedOperationException("Spring Security maneja el login");
    }
}
*/

package co.edu.uniquindio.BarakaLashes.servicio.Implementaciones;

import co.edu.uniquindio.BarakaLashes.DTO.RegistroDTO;
import co.edu.uniquindio.BarakaLashes.modelo.RolUsuario;
import co.edu.uniquindio.BarakaLashes.modelo.Usuario;
import co.edu.uniquindio.BarakaLashes.repositorio.UsuarioRepositorio;
import co.edu.uniquindio.BarakaLashes.servicio.AuthServicio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServicioImpl implements AuthServicio {

    private final UsuarioRepositorio usuarioRepositorio;

    private static final String[] ADMIN_CEDULAS = {
            "1000000000"
    };

    @Override
    @Transactional
    public Usuario registrar(RegistroDTO registroDTO) {
        log.info("=== INICIANDO REGISTRO ===");
        log.info("Email: {}, Cédula: {}", registroDTO.getEmail(), registroDTO.getCedula());

        if (usuarioRepositorio.existsByEmail(registroDTO.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        if (usuarioRepositorio.existsByCedula(registroDTO.getCedula())) {
            throw new RuntimeException("La cédula ya está registrada");
        }

        if (!registroDTO.getPassword().equals(registroDTO.getConfirmarPassword())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        if (registroDTO.getPassword().length() < 6) {
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
        }

        RolUsuario rol = esAdmin(registroDTO.getCedula()) ? RolUsuario.ADMIN : RolUsuario.USUARIO;

        Usuario usuario = new Usuario();
        usuario.setNombre(registroDTO.getNombre());
        usuario.setApellido(registroDTO.getApellido());
        usuario.setCedula(registroDTO.getCedula());
        usuario.setEmail(registroDTO.getEmail());
        usuario.setTelefono(registroDTO.getTelefono());
        usuario.setPassword(registroDTO.getPassword()); // Texto plano
        usuario.setRol(rol);

        Usuario usuarioGuardado = usuarioRepositorio.save(usuario);
        log.info("Usuario guardado con ID: {}", usuarioGuardado.getIdUsuario());

        return usuarioGuardado;
    }

    @Override
    public boolean validarCredenciales(String email, String password) {
        log.info("Validando credenciales para: {}", email);

        return usuarioRepositorio.findByEmail(email)
                .map(usuario -> {
                    boolean matches = password.equals(usuario.getPassword());
                    log.info("Credenciales válidas: {}", matches);
                    return matches;
                })
                .orElse(false);
    }

    @Override
    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepositorio.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    public boolean existeUsuario(String email) {
        return usuarioRepositorio.existsByEmail(email);
    }

    @Override
    public boolean existeCedula(String cedula) {
        return usuarioRepositorio.existsByCedula(cedula);
    }

    private boolean esAdmin(String cedula) {
        for (String adminCedula : ADMIN_CEDULAS) {
            if (adminCedula.equals(cedula)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Usuario login(co.edu.uniquindio.BarakaLashes.DTO.LoginDTO loginDTO) {
        throw new UnsupportedOperationException("Usar validarCredenciales");
    }
}