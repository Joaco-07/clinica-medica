package presentacion;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import datatypes.Franja;
import excepciones.PrestacionRepetidaException;
import interfaces.IControlador;

/** Permite al médico agregar estudios y terapias al catálogo de la clínica. */
public class AltaPrestacion extends VentanaInterna {

    private static final long serialVersionUID = 1L;
    private static final String ESTUDIO = "Estudio";
    private static final String TERAPIA = "Terapia";

    private final JComboBox<String> campoTipo = new JComboBox<>(new String[] { ESTUDIO, TERAPIA });
    private final JTextField campoNombre = new JTextField(18);
    private final JTextField campoPrecio = new JTextField(18);
    private final JComboBox<Franja> campoFranja = new JComboBox<>(Franja.values());
    private final JTextField campoDuracion = new JTextField(8);
    private final JTextField campoSesiones = new JTextField(8);
    private final JCheckBox campoDerivacion = new JCheckBox("Requiere derivación médica");
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel panelDatosEspecificos = new JPanel(tarjetas);

    public AltaPrestacion(IControlador icon) {
        super(icon, "Agregar prestación", 600, 420);

        prepararDatosEspecificos();
        campoTipo.addActionListener(e -> alternarCampos());

        agregarCampo("Tipo de prestación", campoTipo);
        agregarCampo("Nombre", campoNombre);
        agregarCampo("Precio", campoPrecio);
        agregarCampo("Franja horaria", campoFranja);
        agregarCampo("Datos específicos", panelDatosEspecificos);

        agregarBoton("Guardar prestación", this::guardar);
        agregarBoton("Cancelar", this::cancelar);
        alternarCampos();
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

    private void alternarCampos() {
        tarjetas.show(panelDatosEspecificos, esEstudio() ? ESTUDIO : TERAPIA);
        panelDatosEspecificos.revalidate();
        panelDatosEspecificos.repaint();
    }

    private boolean esEstudio() {
        return ESTUDIO.equals(campoTipo.getSelectedItem());
    }

    @Override
    protected void refrescar() {
        if (!isVisible()) {
            limpiar();
        }
    }

    private void guardar() {
        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty()) {
            Mensajes.error(this, "Ingresá el nombre de la prestación.");
            campoNombre.requestFocusInWindow();
            return;
        }

        try {
            double precio = leerPrecio();
            Franja franja = (Franja) campoFranja.getSelectedItem();

            if (esEstudio()) {
                int duracion = leerEnteroPositivo(campoDuracion, "La duración");
                icon.agregarEstudio(nombre, precio, franja, duracion);
            } else {
                int sesiones = leerEnteroPositivo(campoSesiones, "La cantidad de sesiones");
                icon.agregarTerapia(nombre, precio, franja, campoDerivacion.isSelected(), sesiones);
            }

            Mensajes.info(this, "La prestación se agregó correctamente.");
            limpiar();
            cerrar();
        } catch (IllegalArgumentException e) {
            Mensajes.error(this, e.getMessage());
        } catch (PrestacionRepetidaException e) {
            Mensajes.error(this, e.getMessage());
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

    private void cancelar() {
        limpiar();
        cerrar();
    }

    private void limpiar() {
        campoTipo.setSelectedItem(ESTUDIO);
        campoNombre.setText("");
        campoPrecio.setText("");
        campoFranja.setSelectedIndex(0);
        campoDuracion.setText("");
        campoSesiones.setText("");
        campoDerivacion.setSelected(false);
        campoNombre.requestFocusInWindow();
    }
}
