package presentacion;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import datatypes.DtEstudio;
import datatypes.DtPrestacion;
import datatypes.DtTerapia;
import datatypes.Franja;
import excepciones.PrestacionNoEncontradaException;
import excepciones.PrestacionRepetidaException;
import interfaces.IControlador;

/** Permite seleccionar una prestación del catálogo y modificar sus datos. */
public class ModificarPrestacion extends VentanaInterna {

    private static final long serialVersionUID = 1L;
    private static final String ESTUDIO = "Estudio";
    private static final String TERAPIA = "Terapia";

    private final DefaultTableModel modelo = modeloTabla("ID", "Tipo", "Nombre", "Precio", "Franja", "Detalle");
    private final JTable tabla = new JTable(modelo);
    private final List<DtPrestacion> prestaciones = new ArrayList<>();
    private final JTextField campoTipo = new JTextField(18);
    private final JTextField campoNombre = new JTextField(18);
    private final JTextField campoPrecio = new JTextField(18);
    private final JComboBox<Franja> campoFranja = new JComboBox<>(Franja.values());
    private final JTextField campoDuracion = new JTextField(8);
    private final JTextField campoSesiones = new JTextField(8);
    private final JCheckBox campoDerivacion = new JCheckBox("Requiere derivación médica");
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel panelDatosEspecificos = new JPanel(tarjetas);
    private final JButton botonGuardar;
    private DtPrestacion seleccionada;

    public ModificarPrestacion(IControlador icon) {
        super(icon, "Modificar prestación", 880, 560);

        prepararDatosEspecificos();
        campoTipo.setEditable(false);

        agregarCampo("Tipo de prestación", campoTipo);
        agregarCampo("Nombre", campoNombre);
        agregarCampo("Precio", campoPrecio);
        agregarCampo("Franja horaria", campoFranja);
        agregarCampo("Datos específicos", panelDatosEspecificos);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccion();
            }
        });
        agregarCentro(new JScrollPane(tabla));

        botonGuardar = agregarBoton("Guardar cambios", this::guardar);
        agregarBoton("Cancelar", this::cerrar);
        habilitarFormulario(false);
    }

    private void prepararDatosEspecificos() {
        JPanel panelEstudio = new JPanel(new BorderLayout(10, 0));
        panelEstudio.setOpaque(false);
        panelEstudio.add(new JLabel("Duración (minutos)"), BorderLayout.WEST);
        panelEstudio.add(campoDuracion, BorderLayout.CENTER);

        JPanel panelTerapia = new JPanel(new GridLayout(2, 2, 10, 6));
        panelTerapia.setOpaque(false);
        panelTerapia.add(new JLabel("Cantidad de sesiones"));
        panelTerapia.add(campoSesiones);
        panelTerapia.add(new JLabel(""));
        panelTerapia.add(campoDerivacion);

        panelDatosEspecificos.setOpaque(false);
        panelDatosEspecificos.add(panelEstudio, ESTUDIO);
        panelDatosEspecificos.add(panelTerapia, TERAPIA);
    }

    @Override
    protected void refrescar() {
        Long idAnterior = seleccionada == null ? null : seleccionada.id();
        seleccionada = null;
        prestaciones.clear();
        modelo.setRowCount(0);

        List<DtPrestacion> listado = new ArrayList<>(icon.listarPrestaciones());
        listado.sort(Comparator.comparing(DtPrestacion::nombre, String.CASE_INSENSITIVE_ORDER));

        for (DtPrestacion prestacion : listado) {
            prestaciones.add(prestacion);
            modelo.addRow(new Object[] { prestacion.id(), Formato.tipo(prestacion), prestacion.nombre(), Formato.precio(prestacion.precio()), prestacion.franja(), Formato.detalle(prestacion) });
        }

        int filaAnterior = buscarFila(idAnterior);
        if (filaAnterior >= 0) {
            int filaVista = tabla.convertRowIndexToView(filaAnterior);
            tabla.setRowSelectionInterval(filaVista, filaVista);
        } else {
            tabla.clearSelection();
            limpiarFormulario();
            habilitarFormulario(false);
        }
    }

    private int buscarFila(Long id) {
        if (id == null) {
            return -1;
        }
        for (int i = 0; i < prestaciones.size(); i++) {
            if (id.equals(prestaciones.get(i).id())) {
                return i;
            }
        }
        return -1;
    }

    private void cargarSeleccion() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            seleccionada = null;
            limpiarFormulario();
            habilitarFormulario(false);
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        seleccionada = prestaciones.get(filaModelo);
        campoTipo.setText(Formato.tipo(seleccionada));
        campoNombre.setText(seleccionada.nombre());
        campoPrecio.setText(String.valueOf(seleccionada.precio()));
        campoFranja.setSelectedItem(seleccionada.franja());

        if (seleccionada instanceof DtEstudio estudio) {
            tarjetas.show(panelDatosEspecificos, ESTUDIO);
            campoDuracion.setText(String.valueOf(estudio.duracionMinutos()));
            campoSesiones.setText("");
            campoDerivacion.setSelected(false);
        } else if (seleccionada instanceof DtTerapia terapia) {
            tarjetas.show(panelDatosEspecificos, TERAPIA);
            campoDuracion.setText("");
            campoSesiones.setText(String.valueOf(terapia.cantidadSesiones()));
            campoDerivacion.setSelected(terapia.requiereDerivacion());
        }

        habilitarFormulario(true);
        campoNombre.requestFocusInWindow();
    }

    private void guardar() {
        if (seleccionada == null) {
            Mensajes.error(this, "Seleccioná una prestación para modificar.");
            return;
        }

        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty()) {
            Mensajes.error(this, "Ingresá el nombre de la prestación.");
            campoNombre.requestFocusInWindow();
            return;
        }

        try {
            double precio = leerPrecio();
            Franja franja = (Franja) campoFranja.getSelectedItem();
            DtPrestacion modificada;

            if (seleccionada instanceof DtEstudio estudio) {
                int duracion = leerEnteroPositivo(campoDuracion, "La duración");
                modificada = new DtEstudio(estudio.id(), nombre, precio, franja, duracion);
            } else if (seleccionada instanceof DtTerapia terapia) {
                int sesiones = leerEnteroPositivo(campoSesiones, "La cantidad de sesiones");
                modificada = new DtTerapia(terapia.id(), nombre, precio, franja, campoDerivacion.isSelected(), sesiones);
            } else {
                Mensajes.error(this, "No se pudo reconocer el tipo de prestación.");
                return;
            }

            icon.modificarPrestacion(modificada);
            Mensajes.info(this, "La prestación se modificó correctamente.");
            refrescar();
        } catch (IllegalArgumentException e) {
            Mensajes.error(this, e.getMessage());
        } catch (PrestacionRepetidaException | PrestacionNoEncontradaException e) {
            Mensajes.error(this, e.getMessage());
            if (e instanceof PrestacionNoEncontradaException) {
                refrescar();
            }
        }
    }

    private double leerPrecio() {
        String texto = campoPrecio.getText().trim().replace(',', '.');
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("Ingresá el precio de la prestación.");
        }

        try {
            double precio = Double.parseDouble(texto);
            if (!Double.isFinite(precio) || precio <= 0) {
                throw new IllegalArgumentException("El precio debe ser mayor que cero.");
            }
            return precio;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El precio debe ser un número válido.");
        }
    }

    private int leerEnteroPositivo(JTextField campo, String nombreCampo) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            throw new IllegalArgumentException(nombreCampo + " es obligatoria.");
        }

        try {
            int valor = Integer.parseInt(texto);
            if (valor <= 0) {
                throw new IllegalArgumentException(nombreCampo + " debe ser mayor que cero.");
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(nombreCampo + " debe ser un número entero.");
        }
    }

    private void habilitarFormulario(boolean habilitado) {
        campoTipo.setEnabled(habilitado);
        campoNombre.setEnabled(habilitado);
        campoPrecio.setEnabled(habilitado);
        campoFranja.setEnabled(habilitado);
        campoDuracion.setEnabled(habilitado);
        campoSesiones.setEnabled(habilitado);
        campoDerivacion.setEnabled(habilitado);
        botonGuardar.setEnabled(habilitado);
    }

    private void limpiarFormulario() {
        campoTipo.setText("");
        campoNombre.setText("");
        campoPrecio.setText("");
        campoFranja.setSelectedIndex(0);
        campoDuracion.setText("");
        campoSesiones.setText("");
        campoDerivacion.setSelected(false);
        tarjetas.show(panelDatosEspecificos, ESTUDIO);
    }
}
