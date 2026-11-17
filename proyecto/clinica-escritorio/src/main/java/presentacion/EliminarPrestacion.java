package presentacion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import datatypes.DtPrestacion;
import excepciones.PrestacionEnOrdenException;
import excepciones.PrestacionNoEncontradaException;
import interfaces.IControlador;

/** Permite al médico eliminar una prestación que no figure en una orden médica. */
public class EliminarPrestacion extends VentanaInterna {

    private static final long serialVersionUID = 1L;

    private final DefaultTableModel modelo = modeloTabla("ID", "Tipo", "Nombre", "Precio", "Franja", "Detalle");
    private final JTable tabla = new JTable(modelo);
    private final List<DtPrestacion> prestaciones = new ArrayList<>();
    private final JButton botonEliminar;

    public EliminarPrestacion(IControlador icon) {
        super(icon, "Eliminar prestación", 840, 500);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        agregarCentro(new JScrollPane(tabla));

        botonEliminar = agregarBoton("Eliminar prestación", this::eliminar);
        agregarBoton("Cancelar", this::cerrar);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                botonEliminar.setEnabled(tabla.getSelectedRow() >= 0);
            }
        });
        botonEliminar.setEnabled(false);
    }

    @Override
    protected void refrescar() {
        prestaciones.clear();
        modelo.setRowCount(0);

        List<DtPrestacion> listado = new ArrayList<>(icon.listarPrestaciones());
        listado.sort(Comparator.comparing(DtPrestacion::nombre, String.CASE_INSENSITIVE_ORDER));

        for (DtPrestacion prestacion : listado) {
            prestaciones.add(prestacion);
            modelo.addRow(new Object[] { prestacion.id(), Formato.tipo(prestacion), prestacion.nombre(), Formato.precio(prestacion.precio()), prestacion.franja(), Formato.detalle(prestacion) });
        }

        tabla.clearSelection();
        botonEliminar.setEnabled(false);
    }

    private void eliminar() {
        DtPrestacion prestacion = obtenerSeleccionada();
        if (prestacion == null) {
            Mensajes.error(this, "Seleccioná una prestación para eliminar.");
            return;
        }

        String mensaje = "¿Seguro que querés eliminar \"" + prestacion.nombre() + "\"?\n\nEsta acción no se puede deshacer.";
        if (!Mensajes.confirmar(this, mensaje)) {
            return;
        }

        try {
            icon.eliminarPrestacion(prestacion.id());
            Mensajes.info(this, "La prestación se eliminó correctamente.");
            refrescar();
        } catch (PrestacionEnOrdenException e) {
            Mensajes.error(this, e.getMessage());
        } catch (PrestacionNoEncontradaException e) {
            Mensajes.error(this, e.getMessage());
            refrescar();
        }
    }

    private DtPrestacion obtenerSeleccionada() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            return null;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        return prestaciones.get(filaModelo);
    }
}
