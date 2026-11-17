package publicadores;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import datatypes.DtEstudio;
import datatypes.DtLineaOrden;
import datatypes.DtMedico;
import datatypes.DtOrdenMedica;
import datatypes.DtPaciente;
import datatypes.DtPrestacion;
import datatypes.DtSeguido;
import datatypes.DtTerapia;
import datatypes.DtUsuario;
import excepciones.CantidadInvalidaException;
import excepciones.CredencialesInvalidasException;
import excepciones.OrdenSinPrestacionesException;
import excepciones.PrestacionEnOrdenException;
import excepciones.PrestacionNoEncontradaException;
import excepciones.PrestacionRepetidaException;
import excepciones.SeguidoRepetidoException;
import excepciones.UsuarioRepetidoException;
import interfaces.Fabrica;
import interfaces.IControlador;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceException;

/**
 * Publicador SOAP. Las operaciones privadas requieren un token emitido por iniciarSesion.
 * El token y el rol se validan en el servidor; nunca se confía en un rol enviado por el cliente.
 */
@WebService
public class ControladorPublish {

    private final IControlador icon;
    private final Map<String, SesionWS> sesiones = new ConcurrentHashMap<>();
    private static final SecureRandom RANDOM = new SecureRandom();

    private record SesionWS(String email, TipoUsuario tipo) {}

    public ControladorPublish() {
        this.icon = Fabrica.getInstancia().getIControlador();
    }

    // Usuarios: registro público.
    @WebMethod
    public void registrarMedico(String email, String nombre, String password, String especialidad)
            throws UsuarioRepetidoException {
        icon.registrarMedico(email, nombre, password, especialidad);
    }

    @WebMethod
    public void registrarPaciente(String email, String nombre, String password, String mutualista)
            throws UsuarioRepetidoException {
        icon.registrarPaciente(email, nombre, password, mutualista);
    }

    @WebMethod
    public UsuarioWS iniciarSesion(String email, String password) throws CredencialesInvalidasException {
        DtUsuario usuario = icon.iniciarSesion(email, password);
        UsuarioWS ws = aUsuarioWS(usuario);
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        sesiones.put(token, new SesionWS(usuario.email(), ws.getTipo()));
        ws.setToken(token);
        return ws;
    }

    @WebMethod
    public void cerrarSesion(String token) {
        if (token != null) sesiones.remove(token);
    }

    // Prestaciones: solo médicos pueden modificar el catálogo.
    @WebMethod
    public void agregarEstudio(String token, String nombre, double precio, datatypes.Franja franja,
            int duracionMinutos) throws PrestacionRepetidaException {
        exigirMedico(token);
        icon.agregarEstudio(nombre, precio, franja, duracionMinutos);
    }

    @WebMethod
    public void agregarTerapia(String token, String nombre, double precio, datatypes.Franja franja,
            boolean requiereDerivacion, int cantidadSesiones) throws PrestacionRepetidaException {
        exigirMedico(token);
        icon.agregarTerapia(nombre, precio, franja, requiereDerivacion, cantidadSesiones);
    }

    @WebMethod
    public void modificarPrestacion(String token, PrestacionWS prestacion)
            throws PrestacionRepetidaException, PrestacionNoEncontradaException {
        exigirMedico(token);
        if (prestacion == null || prestacion.getId() == null || prestacion.getTipo() == null
                || prestacion.getNombre() == null || prestacion.getFranja() == null) {
            throw new WebServiceException("La prestación, su ID, tipo, nombre y franja son obligatorios.");
        }
        if (prestacion.getPrecio() < 0) throw new WebServiceException("El precio no puede ser negativo.");
        if (prestacion.getTipo() == TipoPrestacion.ESTUDIO
                && (prestacion.getDuracionMinutos() == null || prestacion.getDuracionMinutos() <= 0)) {
            throw new WebServiceException("La duración del estudio debe ser mayor que cero.");
        }
        if (prestacion.getTipo() == TipoPrestacion.TERAPIA
                && (prestacion.getCantidadSesiones() == null || prestacion.getCantidadSesiones() <= 0)) {
            throw new WebServiceException("La cantidad de sesiones debe ser mayor que cero.");
        }
        icon.modificarPrestacion(aDtPrestacion(prestacion));
    }

    @WebMethod
    public void eliminarPrestacion(String token, Long id)
            throws PrestacionEnOrdenException, PrestacionNoEncontradaException {
        exigirMedico(token);
        if (id == null) throw new WebServiceException("El ID de la prestación es obligatorio.");
        icon.eliminarPrestacion(id);
    }

    // Catálogo público.
    @WebMethod public List<PrestacionWS> listarPrestaciones() {
        return aListaPrestacionesWS(icon.listarPrestaciones());
    }
    @WebMethod public List<PrestacionWS> listarPrestacionesPorPrecio() {
        return aListaPrestacionesWS(icon.listarPrestacionesPorPrecio());
    }
    @WebMethod public List<PrestacionWS> listarPrestacionesAlfabeticamente() {
        return aListaPrestacionesWS(icon.listarPrestacionesAlfabeticamente());
    }
    @WebMethod public List<PrestacionWS> buscarPrestacionesPorParteNombre(String nombre) {
        return aListaPrestacionesWS(icon.buscarPrestacionesPorParteNombre(nombre));
    }
    @WebMethod public PrestacionWS obtenerDetallePrestacion(Long id) {
        DtPrestacion p = icon.obtenerDetallePrestacion(id);
        return p == null ? null : aPrestacionWS(p);
    }

    // Seguidas: el email se obtiene del token, no de un parámetro manipulable.
    @WebMethod
    public void agregarSeguido(String token, Long idPrestacion) throws SeguidoRepetidoException {
        SesionWS s = exigirPaciente(token);
        icon.agregarSeguido(s.email(), idPrestacion);
    }
    @WebMethod
    public void quitarSeguido(String token, Long idPrestacion) {
        SesionWS s = exigirPaciente(token);
        icon.quitarSeguido(s.email(), idPrestacion);
    }
    @WebMethod
    public List<SeguidoWS> listarSeguidas(String token) {
        SesionWS s = exigirPaciente(token);
        List<SeguidoWS> resultado = new ArrayList<>();
        for (DtSeguido seguido : icon.listarSeguidas(s.email())) {
            SeguidoWS ws = new SeguidoWS();
            ws.setPrestacion(aPrestacionWS(seguido.prestacion()));
            ws.setFecha(seguido.fecha() == null ? null : seguido.fecha().toString());
            resultado.add(ws);
        }
        return resultado;
    }

    /** El navegador envía todas las líneas en una llamada SOAP; precios siempre del servidor. */
    @WebMethod
    public void realizarOrden(String token, List<LineaSolicitudWS> lineas)
            throws CantidadInvalidaException, PrestacionNoEncontradaException, OrdenSinPrestacionesException {
        SesionWS s = exigirPaciente(token);
        if (lineas == null || lineas.isEmpty()) {
            throw new OrdenSinPrestacionesException("No se puede confirmar una orden médica sin prestaciones.");
        }
        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (LineaSolicitudWS linea : lineas) {
            if (linea == null || linea.getIdPrestacion() == null) {
                throw new WebServiceException("Cada línea debe indicar un ID de prestación.");
            }
            if (linea.getCantidad() <= 0) throw new CantidadInvalidaException("La cantidad debe ser mayor que cero.");
            cantidades.merge(linea.getIdPrestacion(), linea.getCantidad(), Math::addExact);
        }
        for (Long id : cantidades.keySet()) {
            if (icon.obtenerDetallePrestacion(id) == null) {
                throw new PrestacionNoEncontradaException("No existe una prestación con el id: " + id);
            }
        }
        List<Long> agregadas = new ArrayList<>();
        try {
            for (Map.Entry<Long, Integer> e : cantidades.entrySet()) {
                icon.agregarPrestacionSolicitud(s.email(), e.getKey(), e.getValue());
                agregadas.add(e.getKey());
            }
            icon.confirmarOrdenMedica(s.email());
        } catch (CantidadInvalidaException | PrestacionNoEncontradaException | OrdenSinPrestacionesException ex) {
            for (Long id : agregadas) icon.quitarPrestacionSolicitud(s.email(), id);
            throw ex;
        }
    }

    @WebMethod
    public List<OrdenWS> listarOrdenesPaciente(String token) {
        SesionWS s = exigirPaciente(token);
        List<OrdenWS> resultado = new ArrayList<>();
        for (DtOrdenMedica orden : icon.listarOrdenesPaciente(s.email())) {
            OrdenWS ws = new OrdenWS();
            ws.setId(orden.id());
            ws.setFecha(orden.fecha() == null ? null : orden.fecha().toString());
            ws.setTotal(orden.total());
            for (DtLineaOrden linea : orden.lineas()) {
                LineaOrdenWS l = new LineaOrdenWS();
                l.setNombrePrestacion(linea.nombrePrestacion());
                l.setCantidad(linea.cantidad());
                l.setPrecioUnitario(linea.precioUnitario());
                l.setSubtotal(linea.subtotal());
                ws.getLineas().add(l);
            }
            resultado.add(ws);
        }
        resultado.sort((a, b) -> LocalDateTime.parse(b.getFecha()).compareTo(LocalDateTime.parse(a.getFecha())));
        return resultado;
    }

    // Validación de sesión/rol centralizada.
    private SesionWS exigirSesion(String token) {
        SesionWS s = token == null ? null : sesiones.get(token);
        if (s == null) throw new WebServiceException("Sesión inválida o vencida. Iniciá sesión nuevamente.");
        return s;
    }
    private SesionWS exigirMedico(String token) {
        SesionWS s = exigirSesion(token);
        if (s.tipo() != TipoUsuario.MEDICO) throw new WebServiceException("Operación permitida únicamente a médicos.");
        return s;
    }
    private SesionWS exigirPaciente(String token) {
        SesionWS s = exigirSesion(token);
        if (s.tipo() != TipoUsuario.PACIENTE) throw new WebServiceException("Operación permitida únicamente a pacientes.");
        return s;
    }

    // Traducciones entre los DTO SOAP y los datatypes existentes.
    private List<PrestacionWS> aListaPrestacionesWS(List<DtPrestacion> prestaciones) {
        List<PrestacionWS> r = new ArrayList<>();
        if (prestaciones != null) for (DtPrestacion p : prestaciones) r.add(aPrestacionWS(p));
        return r;
    }
    private PrestacionWS aPrestacionWS(DtPrestacion p) {
        PrestacionWS ws = new PrestacionWS();
        ws.setId(p.id()); ws.setNombre(p.nombre()); ws.setPrecio(p.precio()); ws.setFranja(p.franja());
        if (p instanceof DtEstudio e) {
            ws.setTipo(TipoPrestacion.ESTUDIO); ws.setDuracionMinutos(e.duracionMinutos());
        } else if (p instanceof DtTerapia t) {
            ws.setTipo(TipoPrestacion.TERAPIA); ws.setRequiereDerivacion(t.requiereDerivacion());
            ws.setCantidadSesiones(t.cantidadSesiones());
        }
        return ws;
    }
    private DtPrestacion aDtPrestacion(PrestacionWS ws) {
        if (ws.getTipo() == TipoPrestacion.ESTUDIO)
            return new DtEstudio(ws.getId(), ws.getNombre(), ws.getPrecio(), ws.getFranja(), ws.getDuracionMinutos());
        if (ws.getTipo() == TipoPrestacion.TERAPIA)
            return new DtTerapia(ws.getId(), ws.getNombre(), ws.getPrecio(), ws.getFranja(),
                    ws.isRequiereDerivacion(), ws.getCantidadSesiones());
        throw new WebServiceException("Tipo de prestación desconocido.");
    }
    private UsuarioWS aUsuarioWS(DtUsuario u) {
        UsuarioWS ws = new UsuarioWS();
        ws.setEmail(u.email()); ws.setNombre(u.nombre());
        if (u instanceof DtMedico) ws.setTipo(TipoUsuario.MEDICO);
        else if (u instanceof DtPaciente) ws.setTipo(TipoUsuario.PACIENTE);
        return ws;
    }
}
