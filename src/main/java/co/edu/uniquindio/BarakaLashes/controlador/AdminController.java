//package co.edu.uniquindio.BarakaLashes.controlador;
//
//import co.edu.uniquindio.BarakaLashes.modelo.RolUsuario;
//import co.edu.uniquindio.BarakaLashes.modelo.Usuario;
//import co.edu.uniquindio.BarakaLashes.repositorio.CitaRepositorio;
//import co.edu.uniquindio.BarakaLashes.repositorio.UsuarioRepositorio;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.util.List;
//
//@Controller
//@RequestMapping("/admin")
//@RequiredArgsConstructor
//public class AdminController {
//
//    private final UsuarioRepositorio usuarioRepositorio;
//    private final CitaRepositorio citaRepositorio;
//
//    @GetMapping("/dashboard")
//    public String dashboard(HttpSession session, Model model) {
//        // Verificar si es administrador
//        Usuario usuario = (Usuario) session.getAttribute("usuario");
//        if (usuario == null || usuario.getRol() != RolUsuario.ADMIN) {
//            return "redirect:/auth/login";
//        }
//
//        // Estadísticas para el dashboard
//        long totalUsuarios = usuarioRepositorio.count();
//        long totalCitas = citaRepositorio.count();
//        long citasPendientes = citaRepositorio.findByEstadoCita(co.edu.uniquindio.BarakaLashes.modelo.EstadoCita.PENDIENTE).size();
//
//        // Últimos usuarios registrados
//        List<Usuario> ultimosUsuarios = usuarioRepositorio.findAll().stream()
//                .limit(5)
//                .toList();
//
//        model.addAttribute("totalUsuarios", totalUsuarios);
//        model.addAttribute("totalCitas", totalCitas);
//        model.addAttribute("citasPendientes", citasPendientes);
//        model.addAttribute("ultimosUsuarios", ultimosUsuarios);
//        model.addAttribute("usuario", usuario);
//
//        return "admin/dashboard";
//    }
//}