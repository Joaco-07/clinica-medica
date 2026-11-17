package servlets;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.SOAPFaultException;

import publicadores.ControladorPublish;
import publicadores.PrestacionWS;
import publicadores.SeguidoWS;
import publicadores.TipoUsuario;
import publicadores.UsuarioWS;
import publicadores.Franja;

import soap.Servicio;
import soporte.Sesion;
import soporte.Vista;

@WebServlet(urlPatterns = {"/prestaciones", "/prestaciones/*"})
public class PrestacionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private boolean esMedico(HttpServletRequest request) {
        UsuarioWS usuario = Sesion.getUsuario(request);

        return usuario != null
                && usuario.getTipo() == TipoUsuario.MEDICO;
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String ruta = request.getPathInfo();

        // Pantalla independiente para agregar prestaciones.
        if ("/nueva".equals(ruta)) {
            if (!esMedico(request)) {
                response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Solo los médicos pueden agregar prestaciones."
                );
                return;
            }

            request.getRequestDispatcher(
                "/WEB-INF/vistas/nuevaPrestacion.jsp"
            ).forward(request, response);

            return;
        }

        try {
            ControladorPublish port = Servicio.getPort();

            // Pantalla independiente para modificar prestaciones.
            if ("/modificar".equals(ruta)) {
                if (!esMedico(request)) {
                    response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Solo los médicos pueden modificar prestaciones."
                    );
                    return;
                }

                String idTexto = request.getParameter("id");

                if (idTexto == null || idTexto.isBlank()) {
                    response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Falta el identificador de la prestación."
                    );
                    return;
                }

                long id = Long.parseLong(idTexto);

                PrestacionWS prestacion =
                    port.obtenerDetallePrestacion(id);

                if (prestacion == null) {
                    response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "No se encontró la prestación."
                    );
                    return;
                }

                request.setAttribute("prestacion", prestacion);

                request.getRequestDispatcher(
                    "/WEB-INF/vistas/modificarPrestacion.jsp"
                ).forward(request, response);

                return;
            }

            String accion = request.getParameter("accion");

            if ("detalle".equals(accion)) {
                String idTexto = request.getParameter("id");

                if (idTexto == null || idTexto.isBlank()) {
                    response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Falta el identificador de la prestación."
                    );
                    return;
                }

                PrestacionWS prestacion =
                    port.obtenerDetallePrestacion(
                        Long.parseLong(idTexto)
                    );

                if (prestacion == null) {
                    response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "No se encontró la prestación."
                    );
                    return;
                }

                request.setAttribute("prestacion", prestacion);

                boolean yaSeguia = false;
                boolean estadoSeguidasDisponible = true;

                UsuarioWS usuario = Sesion.getUsuario(request);

                if (usuario != null
                        && usuario.getTipo() == TipoUsuario.PACIENTE) {

                    try {
                        String token = Sesion.getToken(request);

                        List<SeguidoWS> seguidas =
                            port.listarSeguidas(token);

                        for (SeguidoWS seguido : seguidas) {
                            if (seguido != null
                                    && seguido.getPrestacion() != null
                                    && Long.valueOf(
                                        seguido.getPrestacion().getId()
                                    ).equals(prestacion.getId())) {

                                yaSeguia = true;
                                break;
                            }
                        }

                    } catch (Exception e) {
                        estadoSeguidasDisponible = false;

                        getServletContext().log(
                            "Error al verificar si la prestación está seguida",
                            e
                        );
                    }
                }

                request.setAttribute("yaSeguia", yaSeguia);
                request.setAttribute(
                    "estadoSeguidasDisponible",
                    estadoSeguidasDisponible
                );

                request.getRequestDispatcher(
                    "/WEB-INF/vistas/detallePrestacion.jsp"
                ).forward(request, response);

                return;
            }

            String busqueda = request.getParameter("busqueda");
            String orden = request.getParameter("orden");

            List<PrestacionWS> prestaciones;

            if (busqueda != null && !busqueda.isBlank()) {
                prestaciones = new ArrayList<>(
                    port.buscarPrestacionesPorParteNombre(busqueda.trim())
                );
                if ("PRECIO".equals(orden)) {
                    prestaciones.sort(Comparator.comparingDouble(PrestacionWS::getPrecio));
                } else {
                    prestaciones.sort(Comparator.comparing(PrestacionWS::getNombre, String.CASE_INSENSITIVE_ORDER));
                }
            } else if ("PRECIO".equals(orden)) {
                prestaciones = port.listarPrestacionesPorPrecio();
            } else {
                prestaciones = port.listarPrestacionesAlfabeticamente();
            }

            request.setAttribute("peticionHtmx", Vista.esHtmx(request));
            request.setAttribute("prestaciones", prestaciones);
            request.setAttribute(
                "busqueda",
                busqueda == null ? "" : busqueda
            );
            request.setAttribute(
                "orden",
                "PRECIO".equals(orden)
                    ? "PRECIO"
                    : "ALFABETICO"
            );

            request.getRequestDispatcher(
                "/WEB-INF/vistas/prestaciones.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "El identificador no es válido."
            );

        } catch (Exception e) {
            getServletContext().log(
                "Error al consultar prestaciones", e
            );

            request.setAttribute("error", "No se pudieron consultar las prestaciones. Intentá nuevamente en unos minutos.");
            request.setAttribute("prestaciones", List.of());
            request.setAttribute("peticionHtmx", Vista.esHtmx(request));
            request.getRequestDispatcher("/WEB-INF/vistas/prestaciones.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String accion = request.getParameter("accion");
        String ruta = request.getPathInfo();

        try {
            String token = Sesion.getToken(request);

            ControladorPublish port = Servicio.getPort();

            switch (accion == null ? "" : accion) {

                case "agregarEstudio": {
                    if (!esMedico(request)) {
                        response.sendError(
                            HttpServletResponse.SC_FORBIDDEN,
                            "Solo los médicos pueden agregar prestaciones."
                        );
                        return;
                    }

                    String nombre = request.getParameter("nombre");

                    double precio = Double.parseDouble(
                        request.getParameter("precio")
                    );

                    Franja franja = Franja.valueOf(
                        request.getParameter("franja")
                    );

                    int duracion = Integer.parseInt(
                        request.getParameter("duracionMinutos")
                    );

                    port.agregarEstudio(
                        token, nombre, precio, franja, duracion
                    );

                    break;
                }

                case "agregarTerapia": {
                    if (!esMedico(request)) {
                        response.sendError(
                            HttpServletResponse.SC_FORBIDDEN,
                            "Solo los médicos pueden agregar prestaciones."
                        );
                        return;
                    }

                    String nombre = request.getParameter("nombre");

                    double precio = Double.parseDouble(
                        request.getParameter("precio")
                    );

                    Franja franja = Franja.valueOf(
                        request.getParameter("franja")
                    );

                    String derivacion = request.getParameter("requiereDerivacion");
                    if (!"true".equals(derivacion) && !"false".equals(derivacion)) {
                        throw new IllegalArgumentException("Indicá si la terapia requiere derivación.");
                    }
                    boolean requiereDerivacion = Boolean.parseBoolean(derivacion);

                    int sesiones = Integer.parseInt(
                        request.getParameter("cantidadSesiones")
                    );

                    port.agregarTerapia(
                        token,
                        nombre,
                        precio,
                        franja,
                        requiereDerivacion,
                        sesiones
                    );

                    break;
                }

                case "modificar": {
                    if (!esMedico(request)) {
                        response.sendError(
                            HttpServletResponse.SC_FORBIDDEN,
                            "Solo los médicos pueden modificar prestaciones."
                        );
                        return;
                    }

                    long id = Long.parseLong(
                        request.getParameter("id")
                    );

                    PrestacionWS prestacion =
                        port.obtenerDetallePrestacion(id);

                    if (prestacion == null) {
                        response.sendError(
                            HttpServletResponse.SC_NOT_FOUND,
                            "No se encontró la prestación."
                        );
                        return;
                    }

                    prestacion.setNombre(
                        request.getParameter("nombre")
                    );

                    prestacion.setPrecio(
                        Double.parseDouble(
                            request.getParameter("precio")
                        )
                    );

                    port.modificarPrestacion(token, prestacion);

                    break;
                }

                case "eliminar": {
                    if (!esMedico(request)) {
                        response.sendError(
                            HttpServletResponse.SC_FORBIDDEN,
                            "Solo los médicos pueden eliminar prestaciones."
                        );
                        return;
                    }

                    long id = Long.parseLong(
                        request.getParameter("id")
                    );

                    port.eliminarPrestacion(token, id);

                    break;
                }

                default:
                    response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Acción de prestación desconocida."
                    );
                    return;
            }

            response.sendRedirect(
                request.getContextPath() + "/prestaciones"
            );

        } catch (Exception e) {
            getServletContext().log(
                "Error al procesar una prestación", e
            );

            String mensaje;
            if (e instanceof SOAPFaultException fault) {
                mensaje = fault.getFault().getFaultString();
            } else if (e instanceof WebServiceException) {
                mensaje = "No se pudo obtener respuesta del servidor. Intentá nuevamente en unos minutos.";
            } else if (e instanceof NumberFormatException) {
                mensaje = "El precio, la duración, las sesiones y el identificador deben ser números válidos.";
            } else {
                mensaje = e.getMessage() == null ? "No se pudo completar la operación." : e.getMessage();
            }

            String error =
                "Error al procesar la prestación: " + mensaje;

            // Si falla el alta, volvemos al formulario de alta.
            if ("/nueva".equals(ruta)
                    && ("agregarEstudio".equals(accion)
                    || "agregarTerapia".equals(accion))) {

                request.setAttribute("error", error);

                request.setAttribute(
                    "tipoPrestacion",
                    request.getParameter("tipoPrestacion")
                );

                request.getRequestDispatcher(
                    "/WEB-INF/vistas/nuevaPrestacion.jsp"
                ).forward(request, response);

                return;
            }

            // Si falla la modificación, mantenemos al usuario
            // en la página de edición y mostramos el error.
            if ("/modificar".equals(ruta)
                    && "modificar".equals(accion)) {

                request.setAttribute("error", error);

                try {
                    long id = Long.parseLong(
                        request.getParameter("id")
                    );

                    PrestacionWS prestacion =
                        Servicio.getPort().obtenerDetallePrestacion(id);

                    if (prestacion != null) {
                        // Conservamos los valores que el usuario intentó guardar.
                        prestacion.setNombre(
                            request.getParameter("nombre")
                        );

                        String precioTexto =
                            request.getParameter("precio");

                        if (precioTexto != null
                                && !precioTexto.isBlank()) {
                            prestacion.setPrecio(
                                Double.parseDouble(precioTexto)
                            );
                        }

                        request.setAttribute(
                            "prestacion",
                            prestacion
                        );
                    }

                } catch (Exception consultaError) {
                    getServletContext().log(
                        "No se pudo recuperar la prestación para mostrar el error",
                        consultaError
                    );
                }

                request.getRequestDispatcher(
                    "/WEB-INF/vistas/modificarPrestacion.jsp"
                ).forward(request, response);

                return;
            }

            try {
                ControladorPublish port = Servicio.getPort();

                request.setAttribute(
                    "prestaciones",
                    port.listarPrestacionesAlfabeticamente()
                );

                request.setAttribute("peticionHtmx", Vista.esHtmx(request));
                request.setAttribute("busqueda", "");
                request.setAttribute("orden", "ALFABETICO");
                request.setAttribute("error", error);

            } catch (Exception consultaError) {
                getServletContext().log(
                    "Error al recargar el catálogo",
                    consultaError
                );

                request.setAttribute("error", error);
            }

            request.getRequestDispatcher(
                "/WEB-INF/vistas/prestaciones.jsp"
            ).forward(request, response);
        }
    }
}
