
package servlets;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.ws.WebServiceException;

import publicadores.OrdenWS;
import soap.Servicio;
import soporte.Sesion;
import soporte.Vista;

@WebServlet("/historial-ordenes")
public class HistorialOrdenesServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<OrdenWS> ordenes =
                    Servicio.getPort().listarOrdenesPaciente(
                            Sesion.getToken(request));

            request.setAttribute("ordenes", ordenes);

        } catch (WebServiceException e) {
            getServletContext().log(
                    "Error al consultar el historial de órdenes", e);

            request.setAttribute(
                    "errorHistorial",
                    "No se pudo consultar el historial. "
                    + "Intentá nuevamente en unos minutos.");
        }

        Vista.pagina(
                request,
                response,
                "historial-ordenes.jsp");
    }
}
