package co.edu.uniquindio.BarakaLashes.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        HttpSession session = request.getSession(false);

        // Rutas públicas que no requieren autenticación
        if (uri.startsWith("/auth/") ||
                uri.startsWith("/css/") ||
                uri.startsWith("/js/") ||
                uri.startsWith("/images/") ||
                uri.equals("/")) {
            return true;
        }

        // Verificar si hay sesión activa
        if (session == null || session.getAttribute("usuarioAutenticado") == null) {
            log.warn("Acceso no autorizado a: {}", uri);
            response.sendRedirect("/auth/login?error=no_autenticado");
            return false;
        }

        // En tu AuthInterceptor, en la parte de verificación de admin:
        if (uri.startsWith("/usuarios")) {
            String rol = (String) session.getAttribute("usuarioRol");
            if (!"ADMIN".equals(rol)) {
                log.warn("Usuario sin permisos de admin intentó acceder a: {}", uri);
                response.sendRedirect("/citas/nueva?error=sin_permisos");
                return false;
            }
        }

        return true;
    }
}