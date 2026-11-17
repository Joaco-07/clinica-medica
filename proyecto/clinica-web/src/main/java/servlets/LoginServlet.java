
package servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.ws.WebServiceException;

import publicadores.UsuarioWS;
import publicadores.CredencialesInvalidasException_Exception;

import soap.Servicio;
import soporte.Sesion;

@WebServlet({ "", "/login" })
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // Si ya inició sesión, lo enviamos al inicio.
        if (Sesion.hayUsuario(request)) {
            response.sendRedirect(request.getContextPath() + "/inicio");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/vistas/login.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Evita iniciar otra sesión sobre una ya autenticada.
        if (Sesion.hayUsuario(request)) {
            response.sendRedirect(request.getContextPath() + "/inicio");
            return;
        }

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            request.setAttribute("error", "Correo o contraseña incorrectos.");
            request.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(request, response);
            return;
        }

        try {
            // Validamos las credenciales contra el servidor SOAP.
            UsuarioWS usuario =
                    Servicio.getPort().iniciarSesion(email.trim(), password);

            // Guardamos el usuario y su token en la sesión HTTP.
            Sesion.iniciar(request, usuario);

            response.sendRedirect(request.getContextPath() + "/inicio");

        } catch (CredencialesInvalidasException_Exception e) {

            request.setAttribute(
                    "error",
                    "Correo o contraseña incorrectos."
            );

            request.getRequestDispatcher("/WEB-INF/vistas/login.jsp")
                   .forward(request, response);
        } catch (WebServiceException e) {
            getServletContext().log("No se pudo iniciar sesión", e);
            request.setAttribute("error", "No se pudo obtener respuesta del servidor. Intentá nuevamente en unos minutos.");
            request.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(request, response);
        }
    }
}
