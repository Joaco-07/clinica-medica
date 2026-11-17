
package servlets;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.ws.WebServiceException;

import publicadores.UsuarioWS;
import soap.Servicio;
import soporte.Sesion;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {

        UsuarioWS usuario = Sesion.getUsuario(request);

        try {
            // Invalidamos el token en el servidor SOAP.
            if (usuario != null && usuario.getToken() != null) {
                Servicio.getPort().cerrarSesion(usuario.getToken());
            }

        } catch (WebServiceException e) {
            getServletContext().log("No se pudo cerrar la sesión en el servidor", e);
        } finally {
            // Cerramos la sesión HTTP aunque falle el servicio SOAP.
            Sesion.cerrar(request);
        }

        response.sendRedirect(request.getContextPath() + "/login");
    }
}
