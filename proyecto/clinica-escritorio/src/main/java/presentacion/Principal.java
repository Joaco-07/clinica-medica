package presentacion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.PropertyVetoException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import datatypes.DtMedico;
import datatypes.DtPaciente;
import datatypes.DtUsuario;
import interfaces.IControlador;

/** Ventana principal; sus opciones cambian de acuerdo con el usuario autenticado. */
public final class Principal extends JFrame {

    private static final long serialVersionUID = 1L;

    private final IControlador icon;
    private final DtUsuario usuario;
    private final JDesktopPane escritorio = new EscritorioClinica();

    public Principal(IControlador icon, DtUsuario usuario) {
        this.icon = icon;
        this.usuario = usuario;

        setTitle("Clínica · " + usuario.nombre());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1180, 720);
        setMinimumSize(new Dimension(940, 600));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        javax.swing.ImageIcon iconoVentana = LogoClinica.icono(40, 40);
        if (iconoVentana != null) {
            setIconImage(iconoVentana.getImage());
        }

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Tema.FONDO);
        raiz.add(crearEncabezado(), BorderLayout.NORTH);
        raiz.add(escritorio, BorderLayout.CENTER);
        setContentPane(raiz);

        JMenuBar barra = new JMenuBar();
        barra.setBackground(Tema.SUPERFICIE);
        barra.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.BORDE));
        setJMenuBar(barra);

        if (usuario instanceof DtMedico) {
            crearMenuMedico(barra);
        } else if (usuario instanceof DtPaciente paciente) {
            crearMenuPaciente(barra, paciente.email());
        }
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout(18, 0));
        encabezado.setBackground(Tema.SUPERFICIE);
        encabezado.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 20));

        JPanel datos = Tema.panel(new BorderLayout(0, 3));
        datos.add(Tema.titulo("Hola, " + usuario.nombre(), 22f), BorderLayout.CENTER);
        String rol = usuario instanceof DtMedico ? "Médico" : "Paciente";
        datos.add(Tema.textoSecundario(rol + " · " + usuario.email()), BorderLayout.SOUTH);
        encabezado.add(datos, BorderLayout.CENTER);

        JPanel acciones = Tema.panel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 12, 0));
        JButton cerrarSesion = Tema.botonSecundario("Cerrar sesión");
        cerrarSesion.addActionListener(evento -> cerrarSesion());
        acciones.add(cerrarSesion);
        acciones.add(new LogoClinica(58));
        encabezado.add(acciones, BorderLayout.EAST);
        return encabezado;
    }

    private void crearMenuMedico(JMenuBar barra) {
        AltaPrestacion alta = new AltaPrestacion(icon);
        ModificarPrestacion modificar = new ModificarPrestacion(icon);
        EliminarPrestacion eliminar = new EliminarPrestacion(icon);
        VerCatalogo catalogo = new VerCatalogo(icon, null);

        JMenu prestaciones = menu("Prestaciones");
        prestaciones.add(item("Agregar prestación", () -> mostrar(alta)));
        prestaciones.add(item("Modificar prestación", () -> mostrar(modificar)));
        prestaciones.add(item("Eliminar prestación", () -> mostrar(eliminar)));
        barra.add(prestaciones);

        JMenu consultas = menu("Catálogo");
        consultas.add(item("Ver catálogo", () -> mostrar(catalogo)));
        barra.add(consultas);
    }

    private void crearMenuPaciente(JMenuBar barra, String emailPaciente) {
        Solicitud solicitud = new Solicitud(icon, emailPaciente);
        VerCatalogo catalogo = new VerCatalogo(icon, emailPaciente, solicitud);
        Seguidas seguidas = new Seguidas(icon, emailPaciente);
        HistorialOrdenes historial = new HistorialOrdenes(icon, emailPaciente);

        JMenu catalogoMenu = menu("Catálogo");
        catalogoMenu.add(item("Ver prestaciones", () -> mostrar(catalogo)));
        barra.add(catalogoMenu);

        JMenu miCuenta = menu("Mi cuenta");
        miCuenta.add(item("Prestaciones seguidas", () -> mostrar(seguidas)));
        miCuenta.add(item("Mi solicitud", () -> mostrar(solicitud)));
        miCuenta.add(item("Historial de órdenes", () -> mostrar(historial)));
        barra.add(miCuenta);
    }

    private JMenu menu(String texto) {
        JMenu menu = new JMenu(texto);
        menu.setFont(Tema.FUENTE_NEGRITA);
        menu.setForeground(Tema.TEXTO);
        return menu;
    }

    private JMenuItem item(String texto, Runnable accion) {
        JMenuItem item = new JMenuItem(texto);
        item.setFont(Tema.FUENTE);
        item.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 18));
        item.addActionListener(evento -> accion.run());
        return item;
    }

    private void mostrar(VentanaInterna ventana) {
        if (ventana.getParent() == null) {
            escritorio.add(ventana);
            int x = Math.max(0, (escritorio.getWidth() - ventana.getWidth()) / 2);
            int y = Math.max(0, (escritorio.getHeight() - ventana.getHeight()) / 2);
            ventana.setLocation(x, y);
        }

        ventana.refrescar();
        if (ventana.isIcon()) {
            try {
                ventana.setIcon(false);
            } catch (PropertyVetoException excepcion) {
                // Si no se puede restaurar, igual intentamos mostrarla al frente.
            }
        }
        ventana.setVisible(true);
        try {
            ventana.setMaximum(true);
        } catch (PropertyVetoException excepcion) {
            // Si no se puede maximizar, conserva su tamaño original.
        }
        ventana.moveToFront();
        try {
            ventana.setSelected(true);
        } catch (PropertyVetoException excepcion) {
            // La ventana sigue visible aunque el sistema no permita seleccionarla.
        }
    }

    private void cerrarSesion() {
        if (!Mensajes.confirmar(this, "¿Querés cerrar la sesión actual?")) {
            return;
        }
        dispose();
        SwingUtilities.invokeLater(() -> new Login(icon).setVisible(true));
    }

    private static final class EscritorioClinica extends JDesktopPane {

        private static final long serialVersionUID = 1L;

        private EscritorioClinica() {
            setBackground(Tema.FONDO);
        }

        @Override
        protected void paintComponent(Graphics graficos) {
            Graphics2D g2 = (Graphics2D) graficos.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setPaint(new GradientPaint(0, 0, Tema.FONDO, getWidth(), getHeight(), new Color(230, 243, 246)));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(new Color(8, 86, 116, 24));
            g2.setFont(Tema.FUENTE.deriveFont(Font.BOLD, 36f));
            String texto = "Gestión de la clínica";
            int ancho = g2.getFontMetrics().stringWidth(texto);
            g2.drawString(texto, Math.max(24, (getWidth() - ancho) / 2), Math.max(70, getHeight() / 2));

            g2.setColor(new Color(31, 45, 51, 90));
            g2.setFont(Tema.FUENTE.deriveFont(15f));
            String ayuda = "Elegí una opción del menú para comenzar";
            int anchoAyuda = g2.getFontMetrics().stringWidth(ayuda);
            g2.drawString(ayuda, Math.max(24, (getWidth() - anchoAyuda) / 2), Math.max(98, getHeight() / 2 + 32));
            g2.dispose();
        }
    }
}
