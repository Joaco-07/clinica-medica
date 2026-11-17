package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.SOAPFaultException;
import publicadores.CantidadInvalidaException_Exception;
import publicadores.LineaSolicitudWS;
import publicadores.OrdenSinPrestacionesException_Exception;
import publicadores.PrestacionNoEncontradaException_Exception;
import publicadores.PrestacionWS;
import soap.Servicio;
import soporte.ItemSolicitud;
import soporte.Sesion;
import soporte.Vista;

@WebServlet("/solicitud")
public class SolicitudServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession();
        String aviso = (String) sesion.getAttribute("avisoSolicitud");
        if (aviso != null) {
            Vista.aviso(request, aviso, Boolean.TRUE.equals(sesion.getAttribute("errorSolicitud")));
            sesion.removeAttribute("avisoSolicitud");
            sesion.removeAttribute("errorSolicitud");
        }

        request.setAttribute("solicitud", Sesion.getSolicitud(request));
        request.setAttribute("total", Sesion.getTotalSolicitud(request));
        try {
            request.setAttribute("prestaciones", Servicio.getPort().listarPrestaciones());
        } catch (WebServiceException e) {
            request.setAttribute("catalogoNoDisponible", true);
        }
        Vista.pagina(request, response, "solicitud.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");
        try {
            switch (accion == null ? "" : accion) {
                case "agregar" -> agregar(request);
                case "sumar" -> {
                    Sesion.cambiarCantidadSolicitud(request, idPrestacion(request), 1);
                    aviso(request, "Cantidad actualizada.", false);
                }
                case "restar" -> {
                    Sesion.cambiarCantidadSolicitud(request, idPrestacion(request), -1);
                    aviso(request, "Cantidad actualizada.", false);
                }
                case "quitar" -> {
                    Sesion.quitarDeSolicitud(request, idPrestacion(request));
                    aviso(request, "Prestación quitada de la solicitud.", false);
                }
                case "confirmar" -> confirmar(request);
                default -> aviso(request, "Acción desconocida.", true);
            }
        } catch (NumberFormatException e) {
            aviso(request, "El identificador y la cantidad deben ser números enteros válidos.", true);
        } catch (IllegalArgumentException e) {
            aviso(request, e.getMessage(), true);
        } catch (ArithmeticException e) {
            aviso(request, "La cantidad indicada es demasiado grande.", true);
        } catch (CantidadInvalidaException_Exception | PrestacionNoEncontradaException_Exception
                | OrdenSinPrestacionesException_Exception e) {
            aviso(request, e.getMessage(), true);
        } catch (SOAPFaultException e) {
            aviso(request, e.getFault().getFaultString(), true);
        } catch (WebServiceException e) {
            aviso(request, "No se pudo obtener respuesta del servidor. La solicitud se conserva. "
                    + "Si estabas confirmando, verificá el historial antes de volver a intentarlo.", true);
        }

        if (Vista.esHtmx(request) && !"confirmar".equals(accion)) {

            HttpSession sesion = request.getSession();

            String mensaje = (String) sesion.getAttribute("avisoSolicitud");
            boolean esError = Boolean.TRUE.equals(
                    sesion.getAttribute("errorSolicitud")
            );

            if (mensaje != null) {
                Vista.aviso(request, mensaje, esError);
                sesion.removeAttribute("avisoSolicitud");
                sesion.removeAttribute("errorSolicitud");
            }

            request.setAttribute("solicitud", Sesion.getSolicitud(request));
            request.setAttribute("total", Sesion.getTotalSolicitud(request));

            Vista.fragmento(
                    request,
                    response,
                    "fragmentos/contenidoSolicitud.jsp"
            );
            return;
        }

        response.sendRedirect(request.getContextPath() + "/solicitud");
    }

    private void agregar(HttpServletRequest request) {
        Long id = idPrestacion(request);
        String textoCantidad = request.getParameter("cantidad");
        int cantidad = textoCantidad == null ? 1 : Integer.parseInt(textoCantidad);
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        PrestacionWS prestacion = Servicio.getPort().obtenerDetallePrestacion(id);
        if (prestacion == null) {
            throw new IllegalArgumentException("La prestación ya no está disponible.");
        }

        Sesion.agregarASolicitud(request, prestacion.getId(), prestacion.getNombre(),
                prestacion.getPrecio(), cantidad);
        aviso(request, "Prestación agregada a la solicitud.", false);
    }

    private void confirmar(HttpServletRequest request)
            throws CantidadInvalidaException_Exception, PrestacionNoEncontradaException_Exception,
            OrdenSinPrestacionesException_Exception {

        List<LineaSolicitudWS> lineas = new ArrayList<>();
        for (ItemSolicitud item : Sesion.getSolicitud(request)) {
            LineaSolicitudWS linea = new LineaSolicitudWS();
            linea.setIdPrestacion(item.getIdPrestacion());
            linea.setCantidad(item.getCantidad());
            lineas.add(linea);
        }

        Servicio.getPort().realizarOrden(Sesion.getToken(request), lineas);
        Sesion.vaciarSolicitud(request);
        aviso(request, "Orden médica confirmada.", false);
    }

    private Long idPrestacion(HttpServletRequest request) {
        long id = Long.parseLong(request.getParameter("id"));
        if (id <= 0) {
            throw new IllegalArgumentException("El identificador debe ser mayor que cero.");
        }
        return id;
    }

    private void aviso(HttpServletRequest request, String texto, boolean esError) {
        request.getSession().setAttribute("avisoSolicitud", texto);
        request.getSession().setAttribute("errorSolicitud", esError);
    }
}
