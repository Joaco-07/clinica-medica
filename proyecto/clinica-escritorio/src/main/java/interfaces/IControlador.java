package interfaces;

import java.util.List;

import datatypes.DtOrdenMedica;
import datatypes.DtPrestacion;
import datatypes.DtSeguido;
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

public interface IControlador {

    // USUARIOS

    void registrarMedico(
            String email,
            String nombre,
            String password,
            String especialidad) throws UsuarioRepetidoException;

    void registrarPaciente(
            String email,
            String nombre,
            String password,
            String mutualista) throws UsuarioRepetidoException;

    DtUsuario iniciarSesion(
            String email,
            String password) throws CredencialesInvalidasException;

    // PRESTACIONES

    void agregarEstudio(
            String nombre,
            double precio,
            Franja franja,
            int duracionMinutos) throws PrestacionRepetidaException;

    void agregarTerapia(
            String nombre,
            double precio,
            Franja franja,
            boolean requiereDerivacion,
            int cantidadSesiones) throws PrestacionRepetidaException;

    void modificarPrestacion(DtPrestacion prestacion)
            throws PrestacionRepetidaException, PrestacionNoEncontradaException;

    void eliminarPrestacion(Long id)
            throws PrestacionEnOrdenException, PrestacionNoEncontradaException;

    List<DtPrestacion> listarPrestaciones();
    

    // OPCIONALES

    // 1. Agregar y quitar una prestación de seguidas
    void agregarSeguido(String emailPaciente, Long idPrestacion)
            throws SeguidoRepetidoException;

    void quitarSeguido(String emailPaciente, Long idPrestacion);

    List<DtSeguido> listarSeguidas(String emailPaciente);

    // 2. Ordenar catálogo por precio y alfabéticamente

    List<DtPrestacion> listarPrestacionesPorPrecio();

    List<DtPrestacion> listarPrestacionesAlfabeticamente();

    // 3. Buscar prestaciones por parte del nombre
    List<DtPrestacion> buscarPrestacionesPorParteNombre(String nombre);

    // 4. Ver detalle de una prestación
    DtPrestacion obtenerDetallePrestacion(Long id);

    // 5. 
    void agregarPrestacionSolicitud(String emailPaciente,Long idPrestacion,int cantidad)
        throws CantidadInvalidaException, PrestacionNoEncontradaException;

    void quitarPrestacionSolicitud(String emailPaciente, Long idPrestacion);

    void confirmarOrdenMedica(String emailPaciente)
        throws OrdenSinPrestacionesException;

    // 6. Ver el historial de ordenes medicas
    List<DtOrdenMedica> listarOrdenesPaciente(String emailPaciente);

}
