//package co.edu.uniquindio.BarakaLashes.servicio;
//
//import co.edu.uniquindio.BarakaLashes.modelo.Usuario;
//import co.edu.uniquindio.BarakaLashes.repositorio.UsuarioRepositorio;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final UsuarioRepositorio usuarioRepositorio;
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        log.info("Intentando autenticar usuario con email: {}", email);
//
//        Usuario usuario = usuarioRepositorio.findByEmail(email)
//                .orElseThrow(() -> {
//                    log.error("Usuario no encontrado: {}", email);
//                    return new UsernameNotFoundException("Usuario no encontrado con email: " + email);
//                });
//
//        log.info("Usuario encontrado: {} - ID: {}", usuario.getEmail(), usuario.getIdUsuario());
//
//        // Crear las autoridades (roles)
//        List<GrantedAuthority> authorities = new ArrayList<>();
//
//        // Si tienes un campo de rol en tu entidad Usuario:
//        // authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));
//
//        // Por defecto, asignar rol USER
//        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//
//        return User.builder()
//                .username(usuario.getEmail())
//                .password(usuario.getPassword())
//                .authorities(authorities)
//                .accountExpired(false)
//                .accountLocked(false)
//                .credentialsExpired(false)
//                .disabled(false)
//                .build();
//    }
//}