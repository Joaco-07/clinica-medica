
package servlets;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.SOAPFaultException;

import publicadores.SeguidoRepetidoException_Exception;

import soap.Servicio;
import soporte.Sesion;
import soporte.Vista;

@WebServlet("/seguidas")
public class SeguidasServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String token = Sesion.getToken(request);

        try {
            request.setAttribute(
                "seguidas",
                Servicio.getPort().listarSeguidas(token)
            );

            HttpSession sesion = request.getSession(false);

            if (sesion != null) {
                Object aviso = sesion.getAttribute("avisoPendiente");
                Object avisoError =
                    sesion.getAttribute("avisoErrorPendiente");

                if (aviso != null) {
                    request.setAttribute("aviso", aviso);
                    request.setAttribute("avisoError", avisoError);

                    sesion.removeAttribute("avisoPendiente");
                    sesion.removeAttribute("avisoErrorPendiente");
                }
            }

            Vista.pagina(request, response, "seguidas.jsp");

        } catch (Exception e) {
            getServletContext().log("No se pudieron cargar las prestaciones seguidas", e);
            Vista.aviso(request, "No se pudieron cargar las prestaciones seguidas. Intentá nuevamente en unos minutos.", true);
            request.setAttribute("seguidas", List.of());
            Vista.pagina(request, response, "seguidas.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String token = Sesion.getToken(request);
        String accion = request.getParameter("accion");
        String idTexto = request.getParameter("id");

        try {
            if (idTexto == null || idTexto.isBlank()) {
                throw new NumberFormatException();
            }

            Long id = Long.valueOf(idTexto);
            if (id <= 0) {
                throw new NumberFormatException();
            }

            if ("agregar".equals(accion)) {

                Servicio.getPort().agregarSeguido(token, id);

                if (Vista.esHtmx(request)) {
                    response.setHeader(
                        "HX-Trigger",
                        "{\"prestacionSeguidasActualizada\":{\"id\":"
                            + id + "}}"
                    );
                }

                Vista.aviso(
                    request,
                    "Prestación agregada a seguidas",
                    false
                );

            } else if ("quitar".equals(accion)) {

                Servicio.getPort().quitarSeguido(token, id);

                if (Vista.esHtmx(request)) {
                    response.setHeader(
                        "HX-Trigger",
                        "{\"prestacionSeguidasQuitada\":{\"id\":"
                            + id + "}}"
                    );
                }

                Vista.aviso(
                    request,
                    "Prestación quitada de seguidas",
                    false
                );

            } else {
                Vista.aviso(request, "Acción desconocida", true);
            }

        } catch (SeguidoRepetidoException_Exception e) {

            Vista.aviso(request, e.getMessage(), true);

        } catch (NumberFormatException e) {

            Vista.aviso(request, "Identificador inválido", true);

        } catch (SOAPFaultException e) {
            Vista.aviso(request, e.getFault().getFaultString(), true);
        } catch (WebServiceException e) {
            getServletContext().log("No se pudieron modificar las prestaciones seguidas", e);
            Vista.aviso(request, "No se pudo obtener respuesta del servidor. Intentá nuevamente en unos minutos.", true);
        }

        if (Vista.esHtmx(request) && "quitar".equals(accion)
                && !"aviso-seguidas".equals(request.getHeader("HX-Target"))) {
            try {
                request.setAttribute(
                    "seguidas",
                    Servicio.getPort().listarSeguidas(token)
                );
            } catch (Exception e) {
                getServletContext().log("No se pudieron actualizar las prestaciones seguidas", e);
                Vista.aviso(request, "No se pudieron actualizar las prestaciones seguidas. Intentá nuevamente en unos minutos.", true);
                Vista.fragmento(request, response, "fragmentos/aviso.jsp");
                return;
            }

            Vista.fragmento(
                request,
                response,
                "fragmentos/listaSeguidas.jsp"
            );
            return;
        }

        if (Vista.esHtmx(request)) {
            Vista.fragmento(
                request,
                response,
                "fragmentos/aviso.jsp"
            );
            return;
        }

        HttpSession sesion = request.getSession();

        sesion.setAttribute(
            "avisoPendiente",
            request.getAttribute("aviso")
        );

        sesion.setAttribute(
            "avisoErrorPendiente",
            request.getAttribute("avisoError")
        );

        response.sendRedirect(
            request.getContextPath() + "/seguidas"
        );
    }
}
