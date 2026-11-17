package servlets;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import publicadores.PrestacionWS;
import publicadores.SeguidoWS;
import publicadores.TipoUsuario;
import publicadores.UsuarioWS;
import soap.Servicio;
import soporte.Sesion;

@WebServlet("/catalogo")
public class CatalogoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String busqueda = request.getParameter("busqueda");
        String orden = request.getParameter("orden");

        if (orden == null || orden.isBlank()) {
            orden = "ALFABETICO";
        }

        List<PrestacionWS> prestaciones = List.of();

        try {
            if (busqueda != null && !busqueda.isBlank()) {
                prestaciones = new ArrayList<>(Servicio.getPort()
                        .buscarPrestacionesPorParteNombre(busqueda.trim()));
                if ("PRECIO".equals(orden)) {
                    prestaciones.sort(Comparator.comparingDouble(PrestacionWS::getPrecio));
                } else {
                    prestaciones.sort(Comparator.comparing(PrestacionWS::getNombre, String.CASE_INSENSITIVE_ORDER));
                }

            } else if ("PRECIO".equals(orden)) {
                prestaciones = Servicio.getPort()
                        .listarPrestacionesPorPrecio();

            } else {
                orden = "ALFABETICO";
                prestaciones = Servicio.getPort()
                        .listarPrestacionesAlfabeticamente();
            }

            request.setAttribute("error", null);

        } catch (Exception e) {
            request.setAttribute(
                    "error",
                    "No se pudo cargar el catálogo. Verificá que el servidor esté funcionando."
            );

            getServletContext().log(
                    "Error al consultar el catálogo de prestaciones",
                    e
            );
        }

        request.setAttribute("prestaciones", prestaciones);
        request.setAttribute(
                "busqueda",
                busqueda == null ? "" : busqueda
        );
        request.setAttribute("orden", orden);
        Set<Long> idsSeguidas = new HashSet<>();
        boolean estadoSeguidasDisponible = true;

        UsuarioWS usuario = Sesion.getUsuario(request);

        if (usuario != null
                && usuario.getTipo() == TipoUsuario.PACIENTE) {

            try {
                String token = Sesion.getToken(request);

                List<SeguidoWS> seguidas =
                        Servicio.getPort().listarSeguidas(token);

                for (SeguidoWS seguido : seguidas) {
                    if (seguido != null
                            && seguido.getPrestacion() != null) {

                        idsSeguidas.add(
                                seguido.getPrestacion().getId()
                        );
                    }
                }

            } catch (Exception e) {
                estadoSeguidasDisponible = false;

                getServletContext().log(
                        "Error al consultar las prestaciones seguidas",
                        e
                );
            }
        }

        request.setAttribute("idsSeguidas", idsSeguidas);
        request.setAttribute(
                "estadoSeguidasDisponible",
                estadoSeguidasDisponible
        );

        if (soporte.Vista.esHtmx(request)) {
            soporte.Vista.fragmento(
                    request,
                    response,
                    "fragmentos/resultadosCatalogo.jsp"
            );
            return;
        }

        soporte.Vista.pagina(
                request,
                response,
                "catalogo.jsp"
        );

    }
}
