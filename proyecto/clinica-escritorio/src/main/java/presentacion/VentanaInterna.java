package presentacion;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import interfaces.IControlador;

/**
 * Base comun de las ventanas internas: agrega encabezado, logo, margenes,
 * formulario, zona central y botones con un estilo consistente.
 */
public abstract class VentanaInterna extends JInternalFrame {

    private static final long serialVersionUID = 1L;

    protected final IControlador icon;

    private final JPanel formulario = Tema.panel(new GridBagLayout());
    private final JPanel centro = Tema.panel(new BorderLayout());
    private final JPanel botones = Tema.panel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    private int filaFormulario;

    protected VentanaInterna(IControlador icon, String titulo, int ancho, int alto) {
        super(titulo, true, true, true, true);
        this.icon = icon;

        setSize(ancho, alto);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setFrameIcon(LogoClinica.icono(20, 20));

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Tema.FONDO);
        setContentPane(raiz);

        JPanel encabezado = new JPanel(new BorderLayout(18, 0));
        encabezado.setBackground(Tema.SUPERFICIE);
        encabezado.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.BORDE), BorderFactory.createEmptyBorder(12, 20, 12, 18)));

        JPanel textos = Tema.panel(new BorderLayout(0, 3));
        textos.add(Tema.titulo(titulo, 20f), BorderLayout.CENTER);
        textos.add(Tema.textoSecundario("Sistema de gestión de la clínica"), BorderLayout.SOUTH);
        encabezado.add(textos, BorderLayout.CENTER);
        encabezado.add(new LogoClinica(54), BorderLayout.EAST);
        raiz.add(encabezado, BorderLayout.NORTH);

        JPanel contenido = Tema.panel(new BorderLayout(0, 16));
        contenido.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        contenido.add(formulario, BorderLayout.NORTH);
        contenido.add(centro, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);
        raiz.add(contenido, BorderLayout.CENTER);
    }

    /** Las ventanas con datos lo sobrescriben para volver a consultar la base. */
    protected void refrescar() {
    }

    /** Agrega una fila de etiqueta y campo al formulario superior. */
    protected void agregarCampo(String etiqueta, Component campo) {
        Tema.estilizarCampo(campo);

        GridBagConstraints restriccionesEtiqueta = new GridBagConstraints();
        restriccionesEtiqueta.gridx = 0;
        restriccionesEtiqueta.gridy = filaFormulario;
        restriccionesEtiqueta.anchor = GridBagConstraints.LINE_START;
        restriccionesEtiqueta.insets = new Insets(0, 0, 10, 16);
        formulario.add(Tema.etiquetaCampo(etiqueta), restriccionesEtiqueta);

        GridBagConstraints restriccionesCampo = new GridBagConstraints();
        restriccionesCampo.gridx = 1;
        restriccionesCampo.gridy = filaFormulario;
        restriccionesCampo.weightx = 1;
        restriccionesCampo.fill = GridBagConstraints.HORIZONTAL;
        restriccionesCampo.insets = new Insets(0, 0, 10, 0);
        formulario.add(campo, restriccionesCampo);
        filaFormulario++;
    }

    /** Agrega un boton y conecta la accion que debe ejecutar. */
    protected JButton agregarBoton(String texto, Runnable accion) {
        JButton boton = Tema.botonAccion(texto);
        boton.addActionListener(evento -> accion.run());
        botones.add(boton);
        return boton;
    }

    /** Coloca una tabla u otro componente en el espacio principal. */
    protected void agregarCentro(Component componente) {
        centro.removeAll();
        if (componente instanceof JScrollPane desplazamiento && desplazamiento.getViewport().getView() instanceof JTable tabla) {
            Tema.estilizarTabla(tabla);
            desplazamiento.setBorder(BorderFactory.createLineBorder(Tema.BORDE, 1, true));
        } else if (componente instanceof JTable tabla) {
            Tema.estilizarTabla(tabla);
        }
        centro.add(componente, BorderLayout.CENTER);
        centro.revalidate();
        centro.repaint();
    }

    /** Modelo de tabla de solo lectura. */
    protected static DefaultTableModel modeloTabla(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    protected void cerrar() {
        setVisible(false);
    }
}
