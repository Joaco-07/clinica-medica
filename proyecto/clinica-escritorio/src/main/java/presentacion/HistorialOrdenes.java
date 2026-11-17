package presentacion;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

import datatypes.DtLineaOrden;
import datatypes.DtOrdenMedica;
import excepciones.PersistenciaException;
import interfaces.IControlador;

/** Muestra las ordenes del paciente, de la mas reciente a la mas antigua. */
public class HistorialOrdenes extends VentanaInterna {

    private static final long serialVersionUID = 1L;

    private final String emailPaciente;
    private final DefaultTableModel modeloOrdenes = modeloTabla("Orden", "Fecha", "Prestaciones", "Total");
    private final JTable tablaOrdenes = new JTable(modeloOrdenes);
    private final DefaultTableModel modeloLineas = modeloTabla("Prestación", "Cantidad", "Precio unitario", "Subtotal");
    private final JTable tablaLineas = new JTable(modeloLineas);
    private final List<DtOrdenMedica> ordenes = new ArrayList<>();

    public HistorialOrdenes(IControlador icon, String emailPaciente) {
        super(icon, "Historial de órdenes", 800, 500);
        this.emailPaciente = emailPaciente;

        tablaOrdenes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaLineas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaOrdenes.getSelectionModel().addListSelectionListener(this::mostrarDetalle);
        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tablaOrdenes), new JScrollPane(tablaLineas));
        division.setResizeWeight(0.5);
        agregarCentro(division);
        agregarBoton("Actualizar", this::refrescar);
        agregarBoton("Cerrar", this::cerrar);
    }

    @Override
    protected void refrescar() {
        ordenes.clear();
        modeloOrdenes.setRowCount(0);
        modeloLineas.setRowCount(0);
        try {
            ordenes.addAll(icon.listarOrdenesPaciente(emailPaciente));
            for (DtOrdenMedica orden : ordenes) {
                int cantidadPrestaciones = orden.lineas().stream().mapToInt(DtLineaOrden::cantidad).sum();
                modeloOrdenes.addRow(new Object[] { orden.id(), Formato.fechaHora(orden.fecha()), cantidadPrestaciones, Formato.precio(orden.total()) });
            }
        } catch (PersistenciaException excepcion) {
            Mensajes.error(this, excepcion.getMessage());
        } catch (RuntimeException excepcion) {
            excepcion.printStackTrace();
            Mensajes.error(this, "Ocurrió un error inesperado al consultar las órdenes médicas.");
        }
    }

    private void mostrarDetalle(ListSelectionEvent evento) {
        if (evento.getValueIsAdjusting()) {
            return;
        }
        modeloLineas.setRowCount(0);
        int filaVista = tablaOrdenes.getSelectedRow();
        if (filaVista < 0) {
            return;
        }
        DtOrdenMedica orden = ordenes.get(tablaOrdenes.convertRowIndexToModel(filaVista));
        for (DtLineaOrden linea : orden.lineas()) {
            modeloLineas.addRow(new Object[] { linea.nombrePrestacion(), linea.cantidad(), Formato.precio(linea.precioUnitario()), Formato.precio(linea.subtotal()) });
        }
    }
}
