package presentacion;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import datatypes.DtPrestacion;
import excepciones.SeguidoRepetidoException;
import interfaces.IControlador;

/** Catálogo de prestaciones compartido por médicos y pacientes. */
public class VerCatalogo extends VentanaInterna {

    private static final long serialVersionUID = 1L;
    private static final String ORDEN_NORMAL = "Sin orden específico";
    private static final String ORDEN_PRECIO = "Precio";
    private static final String ORDEN_ALFABETICO = "Nombre (A-Z)";

    private final String emailPaciente;
    private final Solicitud solicitud;
    private final JComboBox<String> campoOrden = new JComboBox<>(new String[] { ORDEN_NORMAL, ORDEN_PRECIO, ORDEN_ALFABETICO });
    private final JTextField campoBusqueda = new JTextField(18);
    private final DefaultTableModel modelo = modeloTabla("ID", "Nombre", "Tipo", "Precio", "Franja", "Detalle");
    private final JTable tabla = new JTable(modelo);
    private final List<DtPrestacion> prestaciones = new ArrayList<>();

    public VerCatalogo(IControlador icon, String emailPaciente) {
        this(icon, emailPaciente, null);
    }

    public VerCatalogo(IControlador icon, String emailPaciente, Solicitud solicitud) {
        super(icon, "Catálogo de prestaciones", 850, 500);
        this.emailPaciente = emailPaciente;
        this.solicitud = solicitud;

        campoOrden.addActionListener(e -> refrescar());
        campoBusqueda.getDocument().addDocumentListener(new BuscadorAlEscribir(this::refrescar));

        agregarCampo("Orden", campoOrden);
        agregarCampo("Buscar por nombre", campoBusqueda);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(30);
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                if (evento.getClickCount() == 2 && tabla.rowAtPoint(evento.getPoint()) >= 0) {
                    verDetalle();
                }
            }
        });
        agregarCentro(new JScrollPane(tabla));

        agregarBoton("Ver detalle", this::verDetalle);
        if (emailPaciente != null) {
            agregarBoton("Seguir prestación", this::agregarSeguido);
            if (solicitud != null) {
                agregarBoton("Agregar a solicitud", this::agregarSolicitud);
            }
        }
        agregarBoton("Actualizar", this::refrescar);
        agregarBoton("Cerrar", this::cerrar);
    }

    @Override
    protected void refrescar() {
        String texto = campoBusqueda.getText().trim();
        campoOrden.setEnabled(texto.isEmpty());

        prestaciones.clear();
        if (!texto.isEmpty()) {
            prestaciones.addAll(icon.buscarPrestacionesPorParteNombre(texto));
        } else if (ORDEN_PRECIO.equals(campoOrden.getSelectedItem())) {
            prestaciones.addAll(icon.listarPrestacionesPorPrecio());
        } else if (ORDEN_ALFABETICO.equals(campoOrden.getSelectedItem())) {
            prestaciones.addAll(icon.listarPrestacionesAlfabeticamente());
        } else {
            prestaciones.addAll(icon.listarPrestaciones());
        }

        modelo.setRowCount(0);
        for (DtPrestacion prestacion : prestaciones) {
            modelo.addRow(new Object[] { prestacion.id(), prestacion.nombre(), Formato.tipo(prestacion), Formato.precio(prestacion.precio()), prestacion.franja(), Formato.detalle(prestacion) });
        }
    }

    private DtPrestacion seleccionado() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            Mensajes.error(this, "Seleccioná una prestación de la lista.");
            return null;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        return prestaciones.get(filaModelo);
    }

    private void verDetalle() {
        DtPrestacion seleccionada = seleccionado();
        if (seleccionada == null) {
            return;
        }

        DtPrestacion prestacion = icon.obtenerDetallePrestacion(seleccionada.id());
        if (prestacion == null) {
            Mensajes.error(this, "La prestación seleccionada ya no existe.");
            refrescar();
            return;
        }

        String detalle = prestacion.nombre() + "\n\nTipo: " + Formato.tipo(prestacion) + "\nPrecio: " + Formato.precio(prestacion.precio()) + "\nFranja: " + prestacion.franja() + "\n" + Formato.detalle(prestacion);
        Mensajes.info(this, detalle);
    }

    private void agregarSeguido() {
        DtPrestacion prestacion = seleccionado();
        if (prestacion == null) {
            return;
        }

        try {
            icon.agregarSeguido(emailPaciente, prestacion.id());
            Mensajes.info(this, prestacion.nombre() + " fue agregada a tus seguidas.");
        } catch (SeguidoRepetidoException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    private void agregarSolicitud() {
        DtPrestacion prestacion = seleccionado();
        if (prestacion != null) {
            solicitud.agregar(prestacion);
        }
    }
}
