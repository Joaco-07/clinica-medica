package filtros;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import publicadores.TipoUsuario;
import publicadores.UsuarioWS;
import soporte.Sesion;

@WebFilter("/*")
public class FiltroSesion implements Filter {

    @Override
    public void doFilter(ServletRequest solicitud, ServletResponse respuesta, FilterChain cadena)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) solicitud;
        HttpServletResponse response = (HttpServletResponse) respuesta;
        String ruta = request.getServletPath();

        boolean detallePublico = ruta.equals("/prestaciones")
                && (request.getMethod().equals("GET") || request.getMethod().equals("HEAD"))
                && "detalle".equals(request.getParameter("accion"));

        if (ruta.isEmpty() || ruta.equals("/") || ruta.equals("/login")
                || ruta.equals("/registro") || ruta.equals("/catalogo") || detallePublico
                || ruta.startsWith("/css/") || ruta.startsWith("/js/") || ruta.startsWith("/img/")) {
            cadena.doFilter(solicitud, respuesta);
            return;
        }

        UsuarioWS usuario = Sesion.getUsuario(request);
        if (usuario == null || usuario.getToken() == null || usuario.getToken().isBlank()) {
            Sesion.cerrar(request);
            String login = request.getContextPath() + "/login";
            if ("true".equals(request.getHeader("HX-Request"))) {
                response.setHeader("HX-Redirect", login);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                response.sendRedirect(login);
            }
            return;
        }

        if (ruta.equals("/prestaciones") && usuario.getTipo() != TipoUsuario.MEDICO) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Solo los médicos pueden gestionar prestaciones.");
            return;
        }

        if ((ruta.equals("/seguidas") || ruta.equals("/solicitud") || ruta.equals("/historial-ordenes"))
                && usuario.getTipo() != TipoUsuario.PACIENTE) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Esta funcionalidad está disponible para pacientes.");
            return;
        }

        cadena.doFilter(solicitud, respuesta);
    }
}
