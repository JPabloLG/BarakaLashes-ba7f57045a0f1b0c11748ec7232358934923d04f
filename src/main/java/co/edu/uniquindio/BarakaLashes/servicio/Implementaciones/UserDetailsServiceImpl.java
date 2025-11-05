package co.edu.uniquindio.BarakaLashes.servicio.Implementaciones;

import co.edu.uniquindio.BarakaLashes.modelo.Usuario;
import co.edu.uniquindio.BarakaLashes.repositorio.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
/**
 * Implementación de Spring Security de UserDetailsService.
 * Se encarga de cargar los datos de un usuario a partir del email
 * para el proceso de autenticación.
 *
 * loadUserByUsername(String email):
 *   - Busca el usuario en la base de datos usando UsuarioRepositorio.
 *   - Si no lo encuentra, lanza UsernameNotFoundException.
 *   - Convierte el rol del usuario en una lista de GrantedAuthority.
 *   - Devuelve un objeto User de Spring Security con:
 *       • email como username
 *       • password encriptada
 *       • roles/authorities del usuario
 *
 * Este servicio es usado por Spring Security para autenticar usuarios.
 */

public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepositorio usuarioRepositorio;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(usuario.getRol().name())
        );

        return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                authorities
        );
    }
}
