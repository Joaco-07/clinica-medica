package soporte;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import publicadores.UsuarioWS;

public class Sesion {

    private static final String USUARIO = "usuario";
    private static final String SOLICITUD = "solicitud";

    private Sesion() {
    }

    // Guarda el usuario autenticado en la sesión HTTP.
    public static void iniciar(HttpServletRequest request, UsuarioWS usuario) {
        request.getSession();
        request.changeSessionId();
        request.getSession().setAttribute(USUARIO, usuario);
    }

    // Recupera el usuario sin crear una sesión nueva.
    public static UsuarioWS getUsuario(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);

        if (sesion == null) {
            return null;
        }

        return (UsuarioWS) sesion.getAttribute(USUARIO);
    }

    // Comprueba si existe un usuario autenticado.
    public static boolean hayUsuario(HttpServletRequest request) {
        return getUsuario(request) != null;
    }

    // Devuelve el token SOAP del usuario autenticado.
    public static String getToken(HttpServletRequest request) {
        UsuarioWS usuario = getUsuario(request);
        return usuario == null ? null : usuario.getToken();
    }

    // Invalida la sesión HTTP.
    public static void cerrar(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);

        if (sesion != null) {
            sesion.invalidate();
        }
    }

    // La solicitud temporal pertenece a esta sesión, no al servidor central.
    @SuppressWarnings("unchecked")
    private static Map<Long, ItemSolicitud> mapaSolicitud(HttpServletRequest request) {
        HttpSession sesion = request.getSession();
        Map<Long, ItemSolicitud> solicitud =
                (Map<Long, ItemSolicitud>) sesion.getAttribute(SOLICITUD);
        if (solicitud == null) {
            solicitud = new LinkedHashMap<>();
            sesion.setAttribute(SOLICITUD, solicitud);
        }
        return solicitud;
    }

    public static Collection<ItemSolicitud> getSolicitud(HttpServletRequest request) {
        return mapaSolicitud(request).values();
    }

    public static void agregarASolicitud(HttpServletRequest request, Long id, String nombre,
            double precio, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        Map<Long, ItemSolicitud> solicitud = mapaSolicitud(request);
        ItemSolicitud item = solicitud.get(id);
        if (item == null) {
            solicitud.put(id, new ItemSolicitud(id, nombre, precio, cantidad));
        } else {
            item.sumar(cantidad);
        }
    }

    public static void cambiarCantidadSolicitud(HttpServletRequest request, Long id, int delta) {
        ItemSolicitud item = mapaSolicitud(request).get(id);
        if (item != null) {
            item.sumar(delta);
            if (item.getCantidad() <= 0) {
                quitarDeSolicitud(request, id);
            }
        }
    }

    public static void quitarDeSolicitud(HttpServletRequest request, Long id) {
        mapaSolicitud(request).remove(id);
    }

    public static void vaciarSolicitud(HttpServletRequest request) {
        mapaSolicitud(request).clear();
    }

    public static double getTotalSolicitud(HttpServletRequest request) {
        return getSolicitud(request).stream().mapToDouble(ItemSolicitud::getSubtotal).sum();
    }
}