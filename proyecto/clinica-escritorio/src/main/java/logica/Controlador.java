package logica;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

import datatypes.DtEstudio;
import datatypes.DtOrdenMedica;
import datatypes.DtPrestacion;
import datatypes.DtSeguido;
import datatypes.DtTerapia;
import datatypes.DtUsuario;
import datatypes.Franja;
import excepciones.CantidadInvalidaException;
import excepciones.CredencialesInvalidasException;
import excepciones.OrdenSinPrestacionesException;
import excepciones.PrestacionEnOrdenException;
import excepciones.PrestacionNoEncontradaException;
import excepciones.PrestacionRepetidaException;
import excepciones.SeguidoRepetidoException;
import excepciones.UsuarioRepetidoException;
import interfaces.IControlador;

public class Controlador implements IControlador {

    private static Controlador instancia;

    private Map<String, OrdenMedica> solicitudes = new HashMap<>(); //creo un hash para ir agregando solicitudes

    private Controlador() {
    }

    public static Controlador getInstancia() {
        if (instancia == null) {
            instancia = new Controlador();
        }

        return instancia;
    }

    //PASSWORD

    private String transformarPassword(String password) {
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    } catch (Exception e) {
        throw new RuntimeException("Error al transformar la contraseña.", e);
    }
    }

    //USUARIOS

    @Override

    public void registrarMedico(
            String email,
            String nombre,
            String password,
            String especialidad) throws UsuarioRepetidoException {
                ManejadorUsuario manejador = ManejadorUsuario.getInstancia();

                if (manejador.buscarUsuario(email) != null) {
                  throw new UsuarioRepetidoException("Ya existe un usuario con el email: " + email);
                }

                Medico medico = new Medico(
                        email,
                        nombre,
                        transformarPassword(password),
                        especialidad
                );
                manejador.agregarUsuario(medico);
            }

    @Override
    public void registrarPaciente(
            String email,
            String nombre,
            String password,
            String mutualista) throws UsuarioRepetidoException {
                
                ManejadorUsuario manejador = ManejadorUsuario.getInstancia();
                
                if (manejador.buscarUsuario(email) != null) {
                    throw new UsuarioRepetidoException("Ya existe un usuario con el email: " + email);
                }

            Paciente paciente = new Paciente(email, nombre, transformarPassword(password), mutualista);
            manejador.agregarUsuario(paciente);
        }

    @Override
    public DtUsuario iniciarSesion(String email, String password) 
           throws CredencialesInvalidasException {
                if (email == null || email.isBlank() || password == null || password.isBlank()) {
                    throw new CredencialesInvalidasException("Email o contraseña incorrectos.");
                }
                ManejadorUsuario manejador = ManejadorUsuario.getInstancia();

                Usuario usuario = manejador.buscarUsuario(email.trim());
                
                if (usuario == null || !usuario.getPassword().equals(transformarPassword(password))) {
                    throw new CredencialesInvalidasException("Email o contraseña incorrectos.");
                }
                return usuario.getDtUsuario();
        }

    private void validarPrestacion(String nombre, double precio, Franja franja) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la prestación es obligatorio.");
        }
        if (!Double.isFinite(precio) || precio < 0) {
            throw new IllegalArgumentException("El precio debe ser un número válido mayor o igual que cero.");
        }
        if (franja == null) {
            throw new IllegalArgumentException("Seleccioná una franja horaria válida.");
        }
    }

    //PRESTACIONES

    @Override
    public void agregarEstudio(
            String nombre,
            double precio,
            Franja franja,
            int duracionMinutos) 
            throws PrestacionRepetidaException {
                validarPrestacion(nombre, precio, franja);
                if (duracionMinutos <= 0) {
                    throw new IllegalArgumentException("La duración del estudio debe ser mayor que cero.");
                }
                ManejadorPrestacion manejador = ManejadorPrestacion.getInstancia();
                
                if (manejador.buscarPrestacionPorNombre(nombre) != null) {
                    throw new PrestacionRepetidaException("Ya existe una prestacion con el nombre: " + nombre);
                }

                Estudio estudio = new Estudio(nombre, precio, franja, duracionMinutos);
                manejador.agregarPrestacion(estudio);
            }

    @Override
    public void agregarTerapia(
            String nombre,
            double precio,
            Franja franja,
            boolean requiereDerivacion,
            int cantidadSesiones) throws PrestacionRepetidaException {
                validarPrestacion(nombre, precio, franja);
                if (cantidadSesiones <= 0) {
                    throw new IllegalArgumentException("La cantidad de sesiones debe ser mayor que cero.");
                }
                ManejadorPrestacion manejador = ManejadorPrestacion.getInstancia();

                if (manejador.buscarPrestacionPorNombre(nombre) != null) {
                    throw new PrestacionRepetidaException("Ya existe una prestacion con el nombre: " + nombre);
                }

                Terapia terapia = new Terapia(nombre, precio, franja, requiereDerivacion, cantidadSesiones);
                manejador.agregarPrestacion(terapia);
            }

    @Override
    public void modificarPrestacion(DtPrestacion prestacion)
            throws PrestacionRepetidaException, PrestacionNoEncontradaException {

                if (prestacion == null || prestacion.id() == null) {
                    throw new IllegalArgumentException("La prestación y su identificador son obligatorios.");
                }
                validarPrestacion(prestacion.nombre(), prestacion.precio(), prestacion.franja());
                if (prestacion instanceof DtEstudio estudio && estudio.duracionMinutos() <= 0) {
                    throw new IllegalArgumentException("La duración del estudio debe ser mayor que cero.");
                }
                if (prestacion instanceof DtTerapia terapia && terapia.cantidadSesiones() <= 0) {
                    throw new IllegalArgumentException("La cantidad de sesiones debe ser mayor que cero.");
                }
                ManejadorPrestacion manejador = ManejadorPrestacion.getInstancia();

                Prestacion existente = manejador.buscarPrestacion(prestacion.id()); //Me fijo si la prestacion existe
                
                if (existente == null) {
                     throw new PrestacionNoEncontradaException("No existe una prestacion con el id: " + prestacion.id());
                }

                Prestacion otra = manejador.buscarPrestacionPorNombre(prestacion.nombre());
                
                if (otra != null && !otra.getId().equals(prestacion.id())) {
                    throw new PrestacionRepetidaException("Ya existe una prestacion con el nombre: " + prestacion.nombre());
                }

                existente.setNombre(prestacion.nombre());
                existente.setPrecio(prestacion.precio());
                existente.setFranja(prestacion.franja());
                if (existente instanceof Estudio estudio && prestacion instanceof DtEstudio dtEstudio) {
                    estudio.setDuracionMinutos(dtEstudio.duracionMinutos());
                } else if (existente instanceof Terapia terapia && prestacion instanceof DtTerapia dtTerapia) {
                    terapia.setRequiereDerivacion(dtTerapia.requiereDerivacion());
                    terapia.setCantidadSesiones(dtTerapia.cantidadSesiones());
                }
                
                manejador.actualizarPrestacion(existente);
            }

    @Override
    public void eliminarPrestacion(Long id)
            throws PrestacionEnOrdenException, PrestacionNoEncontradaException {
                
                ManejadorPrestacion manejador = ManejadorPrestacion.getInstancia();
                Prestacion prestacion = manejador.buscarPrestacion(id);
                
                if (prestacion == null) {
                    throw new PrestacionNoEncontradaException("No existe una prestacion con el id: " + id);
                }

                if (manejador.fueOrdenada(prestacion)) {
                    throw new PrestacionEnOrdenException("La prestacion no se puede eliminar porque fue incluida en una orden.");
                }
                
                manejador.eliminarPrestacion(prestacion);
            }

    @Override
    public List<DtPrestacion> listarPrestaciones() {
        ManejadorPrestacion manejador = ManejadorPrestacion.getInstancia();

        return manejador.listarPrestaciones().stream().map(Prestacion::getDtPrestacion).toList();
         }

//Opcionales

    // OPCIONAL 1

    @Override
    public void agregarSeguido(String emailPaciente, Long idPrestacion)
            throws SeguidoRepetidoException {

                ManejadorSeguido manejador = ManejadorSeguido.getInstancia();

                manejador.agregarSeguido(emailPaciente, idPrestacion);
            }

    @Override
    public void quitarSeguido(String emailPaciente, Long idPrestacion) {

        ManejadorSeguido manejador = ManejadorSeguido.getInstancia();

        manejador.quitarSeguido(emailPaciente, idPrestacion);
    }

    @Override
    public List<DtSeguido> listarSeguidas(String emailPaciente) {

        ManejadorSeguido manejador = ManejadorSeguido.getInstancia();

        return manejador.listarSeguidas(emailPaciente).stream().map(Seguido::getDtSeguido).toList();
    }


    // OPCIONAL 2

    @Override
    public List<DtPrestacion> listarPrestacionesPorPrecio() {

        ManejadorPrestacion manejador = ManejadorPrestacion.getInstancia();

        return manejador.listarPrestacionesPorPrecio().stream().map(Prestacion::getDtPrestacion).toList();
    }

    @Override
    public List<DtPrestacion> listarPrestacionesAlfabeticamente() {

        ManejadorPrestacion manejador = ManejadorPrestacion.getInstancia();

        return manejador.listarPrestacionesAlfabeticamente().stream().map(Prestacion::getDtPrestacion).toList();
    }


    // OPCIONAL 3

    @Override
    public List<DtPrestacion> buscarPrestacionesPorParteNombre(String nombre) {

        ManejadorPrestacion manejador = ManejadorPrestacion.getInstancia();

        return manejador.buscarPrestacionesPorParteNombre(nombre).stream().map(Prestacion::getDtPrestacion).toList();
    }


    // OPCIONAL 4

    @Override
    public DtPrestacion obtenerDetallePrestacion(Long id) {

        ManejadorPrestacion manejador = ManejadorPrestacion.getInstancia();

        return manejador.obtenerDetallePrestacion(id);
    }

   // OPCIONAL 5

    @Override
    public void agregarPrestacionSolicitud(
            String emailPaciente,
            Long idPrestacion,
            int cantidad)
            throws CantidadInvalidaException, PrestacionNoEncontradaException {
                if (cantidad <= 0) {
                    throw new CantidadInvalidaException("La cantidad debe ser mayor que cero.");
                }
                ManejadorUsuario manejadorUsuario =  ManejadorUsuario.getInstancia();
                ManejadorPrestacion manejadorPrestacion = ManejadorPrestacion.getInstancia();
                Usuario usuario = manejadorUsuario.buscarUsuario(emailPaciente);

                if (!(usuario instanceof Paciente paciente)) {  //Si no es paciente sale
                    return;
                }

                Prestacion prestacion = manejadorPrestacion.buscarPrestacion(idPrestacion);

                if (prestacion == null) {
                    throw new PrestacionNoEncontradaException("No existe una prestacion con el id: " + idPrestacion);
                }

                OrdenMedica solicitud = solicitudes.get(emailPaciente);

                if (solicitud == null) {
                    solicitud = new OrdenMedica(paciente);
                    solicitudes.put(emailPaciente, solicitud);
                }
                solicitud.quitarPrestacion(idPrestacion);
                solicitud.agregarLinea(prestacion, cantidad);
            }


    @Override
    public void quitarPrestacionSolicitud(String emailPaciente, Long idPrestacion) {

        OrdenMedica solicitud = solicitudes.get(emailPaciente);

        if (solicitud != null) {
            solicitud.quitarPrestacion(idPrestacion);
        }
    }


    @Override
    public void confirmarOrdenMedica(String emailPaciente)
            throws OrdenSinPrestacionesException {
                OrdenMedica solicitud = solicitudes.get(emailPaciente);
                
                if (solicitud == null || solicitud.getLineas().isEmpty()) {
                    throw new OrdenSinPrestacionesException( "No se puede confirmar una orden médica " + "sin prestaciones.");
                }
                
                ManejadorOrden manejadorOrden = ManejadorOrden.getInstancia();
                manejadorOrden.agregarOrden(solicitud);
                solicitudes.remove(emailPaciente);
    } 

    // OPCIONAL 6

    @Override
    public List<DtOrdenMedica> listarOrdenesPaciente(String emailPaciente) {
        Usuario usuario = ManejadorUsuario.getInstancia().buscarUsuario(emailPaciente);
        if (!(usuario instanceof Paciente paciente)) {
            return List.of();
        }
        return ManejadorOrden.getInstancia().listarOrdenesPaciente(paciente).stream().map(OrdenMedica::getDtOrdenMedica).toList();
    }


}
