package servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.SOAPFaultException;
import publicadores.ControladorPublish;
import publicadores.UsuarioRepetidoException_Exception;
import soap.Servicio;
import soporte.Sesion;
import soporte.Vista;

/** Registro público de médicos y pacientes mediante el servicio SOAP. */
@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (Sesion.hayUsuario(request)) {
            response.sendRedirect(request.getContextPath() + "/inicio");
            return;
        }
        request.setAttribute("tipo", "PACIENTE");
        Vista.pagina(request, response, "registro.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        if (Sesion.hayUsuario(request)) {
            response.sendRedirect(request.getContextPath() + "/inicio");
            return;
        }

        String email = texto(request, "email");
        String nombre = texto(request, "nombre");
        String password = request.getParameter("password");
        String tipo = texto(request, "tipo");
        String extra = texto(request, "extra");

        // Conservamos los campos del formulario ante un error, excepto la contraseña.
        request.setAttribute("email", email);
        request.setAttribute("nombre", nombre);
        request.setAttribute("tipo", tipo);
        request.setAttribute("extra", extra);

        if (email.isBlank() || nombre.isBlank() || password == null
                || password.isBlank() || extra.isBlank()) {
            request.setAttribute("error", "Completá todos los campos.");
        } else if (!"MEDICO".equals(tipo) && !"PACIENTE".equals(tipo)) {
            request.setAttribute("error", "Seleccioná médico o paciente.");
        } else {
            try {
                ControladorPublish port = Servicio.getPort();
                if ("MEDICO".equals(tipo)) {
                    port.registrarMedico(email, nombre, password, extra);
                } else {
                    port.registrarPaciente(email, nombre, password, extra);
                }

                // Redirigimos para que actualizar la página no repita el registro.
                request.getSession().setAttribute("registroExitoso", true);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            } catch (UsuarioRepetidoException_Exception e) {
                request.setAttribute("error", e.getMessage());
            } catch (SOAPFaultException e) {
                request.setAttribute("error", e.getFault().getFaultString());
            } catch (WebServiceException e) {
                request.setAttribute("error",
                        "No se pudo obtener respuesta del servidor. Intentá iniciar sesión "
                        + "para comprobar si la cuenta se creó antes de volver a registrarte.");
            }
        }

        Vista.pagina(request, response, "registro.jsp");
    }

    private String texto(HttpServletRequest request, String parametro) {
        String valor = request.getParameter(parametro);
        return valor == null ? "" : valor.trim();
    }
}
