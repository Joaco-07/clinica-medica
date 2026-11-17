package presentacion;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import datatypes.DtSeguido;
import interfaces.IControlador;

/** Prestaciones que el paciente marcó para seguir. */
public class Seguidas extends VentanaInterna {

    private static final long serialVersionUID = 1L;

    private final String emailPaciente;
    private final DefaultTableModel modelo = modeloTabla("Nombre", "Tipo", "Precio", "Franja", "Seguida desde");
    private final JTable tabla = new JTable(modelo);
    private final List<DtSeguido> seguidas = new ArrayList<>();

    public Seguidas(IControlador icon, String emailPaciente) {
        super(icon, "Mis prestaciones seguidas", 760, 420);
        this.emailPaciente = emailPaciente;

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(30);
        agregarCentro(new JScrollPane(tabla));

        agregarBoton("Quitar de seguidas", this::quitar);
        agregarBoton("Actualizar", this::refrescar);
        agregarBoton("Cerrar", this::cerrar);
    }

    @Override
    protected void refrescar() {
        seguidas.clear();
        seguidas.addAll(icon.listarSeguidas(emailPaciente));

        modelo.setRowCount(0);
        for (DtSeguido seguido : seguidas) {
            modelo.addRow(new Object[] { seguido.prestacion().nombre(), Formato.tipo(seguido.prestacion()), Formato.precio(seguido.prestacion().precio()), seguido.prestacion().franja(), Formato.fecha(seguido.fecha()) });
        }
    }

    private void quitar() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            Mensajes.error(this, "Seleccioná una prestación de la lista.");
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        DtSeguido seguido = seguidas.get(filaModelo);
        String nombre = seguido.prestacion().nombre();

        if (!Mensajes.confirmar(this, "¿Querés quitar " + nombre + " de tus seguidas?")) {
            return;
        }

        icon.quitarSeguido(emailPaciente, seguido.prestacion().id());
        refrescar();
        Mensajes.info(this, nombre + " fue quitada de tus seguidas.");
    }
}
