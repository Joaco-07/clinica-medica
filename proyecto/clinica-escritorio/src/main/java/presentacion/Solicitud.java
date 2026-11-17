package presentacion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import datatypes.DtPrestacion;
import excepciones.CantidadInvalidaException;
import excepciones.OrdenSinPrestacionesException;
import excepciones.PersistenciaException;
import excepciones.PrestacionNoEncontradaException;
import interfaces.IControlador;

/** Solicitud en memoria que el paciente puede modificar antes de confirmar la orden. */
public class Solicitud extends VentanaInterna {

    private static final long serialVersionUID = 1L;

    private final String emailPaciente;
    private final Map<DtPrestacion, Integer> items = new LinkedHashMap<>();
    private final List<DtPrestacion> orden = new ArrayList<>();
    private final DefaultTableModel modelo = modeloTabla("Prestación", "Tipo", "Precio", "Cantidad", "Subtotal");
    private final JTable tabla = new JTable(modelo);
    private final JLabel etiquetaTotal = new JLabel();

    public Solicitud(IControlador icon, String emailPaciente) {
        super(icon, "Mi solicitud", 760, 440);
        this.emailPaciente = emailPaciente;

        agregarCampo("Total", etiquetaTotal);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        agregarCentro(new JScrollPane(tabla));
        agregarBoton("Uno menos", () -> cambiarCantidad(-1));
        agregarBoton("Uno más", () -> cambiarCantidad(1));
        agregarBoton("Quitar", this::quitar);
        agregarBoton("Confirmar orden", this::confirmar);
        agregarBoton("Cerrar", this::cerrar);
        refrescar();
    }

    public void agregar(DtPrestacion prestacion) {
        DtPrestacion existente = buscarPorId(prestacion.id());
        int cantidad = existente == null ? 1 : items.get(existente) + 1;
        if (guardarCantidad(prestacion, existente, cantidad)) {
            Mensajes.info(this, prestacion.nombre() + " fue agregada a la solicitud.");
        }
    }

    @Override
    protected void refrescar() {
        orden.clear();
        orden.addAll(items.keySet());
        modelo.setRowCount(0);
        double total = 0;
        for (DtPrestacion prestacion : orden) {
            int cantidad = items.get(prestacion);
            double subtotal = prestacion.precio() * cantidad;
            total += subtotal;
            modelo.addRow(new Object[] { prestacion.nombre(), Formato.tipo(prestacion), Formato.precio(prestacion.precio()), cantidad, Formato.precio(subtotal) });
        }
        etiquetaTotal.setText(Formato.precio(total));
    }

    private DtPrestacion buscarPorId(Long id) {
        return items.keySet().stream().filter(prestacion -> prestacion.id().equals(id)).findFirst().orElse(null);
    }

    private DtPrestacion seleccionado() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            Mensajes.error(this, "Seleccioná una prestación de la solicitud.");
            return null;
        }
        return orden.get(tabla.convertRowIndexToModel(filaVista));
    }

    private void cambiarCantidad(int cambio) {
        DtPrestacion prestacion = seleccionado();
        if (prestacion == null) {
            return;
        }
        int cantidad = items.get(prestacion) + cambio;
        if (cantidad <= 0) {
            quitar(prestacion);
        } else {
            guardarCantidad(prestacion, prestacion, cantidad);
        }
    }

    private boolean guardarCantidad(DtPrestacion prestacion, DtPrestacion existente, int cantidad) {
        try {
            icon.agregarPrestacionSolicitud(emailPaciente, prestacion.id(), cantidad);
            if (existente != null) {
                items.remove(existente);
            }
            items.put(prestacion, cantidad);
            refrescar();
            return true;
        } catch (CantidadInvalidaException | PrestacionNoEncontradaException excepcion) {
            Mensajes.error(this, excepcion.getMessage());
            return false;
        } catch (PersistenciaException excepcion) {
            Mensajes.error(this, excepcion.getMessage());
            return false;
        } catch (RuntimeException excepcion) {
            excepcion.printStackTrace();
            Mensajes.error(this, "Ocurrió un error inesperado al modificar la solicitud.");
            return false;
        }
    }

    private void quitar() {
        DtPrestacion prestacion = seleccionado();
        if (prestacion == null) {
            return;
        }
        if (Mensajes.confirmar(this, "¿Querés quitar " + prestacion.nombre() + " de la solicitud?")) {
            quitar(prestacion);
        }
    }

    private void quitar(DtPrestacion prestacion) {
        icon.quitarPrestacionSolicitud(emailPaciente, prestacion.id());
        items.remove(prestacion);
        refrescar();
    }

    private void confirmar() {
        try {
            icon.confirmarOrdenMedica(emailPaciente);
            items.clear();
            refrescar();
            Mensajes.info(this, "La orden médica se confirmó correctamente.");
            cerrar();
        } catch (OrdenSinPrestacionesException excepcion) {
            Mensajes.error(this, excepcion.getMessage());
        } catch (PersistenciaException excepcion) {
            Mensajes.error(this, excepcion.getMessage());
        } catch (RuntimeException excepcion) {
            excepcion.printStackTrace();
            Mensajes.error(this, "Ocurrió un error inesperado al confirmar la orden médica.");
        }
    }
}
