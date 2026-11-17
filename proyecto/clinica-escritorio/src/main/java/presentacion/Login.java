package presentacion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import datatypes.DtUsuario;
import excepciones.CredencialesInvalidasException;
import excepciones.PersistenciaException;
import interfaces.Fabrica;
import interfaces.IControlador;

/** Punto de entrada de la aplicacion y pantalla de inicio de sesion. */
public final class Login extends JFrame {

    private static final long serialVersionUID = 1L;

    private final IControlador icon;
    private final JTextField campoEmail = new JTextField(22);
    private final JPasswordField campoPassword = new JPasswordField(22);
    private final JButton botonIngresar = Tema.botonPrimario("Ingresar");

    public static void main(String[] argumentos) {
        SwingUtilities.invokeLater(() -> {
            Tema.instalar();
            new Login().setVisible(true);
        });
    }

    public Login() {
        this(obtenerControlador());
    }

    /** Se usa al cerrar sesion para conservar la misma instancia del controlador. */
    Login(IControlador icon) {
        this.icon = icon;

        setTitle("Clínica · Iniciar sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(920, 570);
        setMinimumSize(new Dimension(820, 520));
        setLocationRelativeTo(null);
        ImageIconUtil.aplicarIcono(this);

        JPanel raiz = new JPanel(new java.awt.GridLayout(1, 2));
        raiz.add(crearMarca());
        raiz.add(crearAcceso());
        setContentPane(raiz);

        getRootPane().setDefaultButton(botonIngresar);
        campoEmail.requestFocusInWindow();
    }

    private static IControlador obtenerControlador() {
        Tema.instalar();
        return Fabrica.getInstancia().getIControlador();
    }

    private JPanel crearMarca() {
        JPanel marca = new PanelDegradado();
        marca.setLayout(new GridBagLayout());
        marca.setBorder(BorderFactory.createEmptyBorder(48, 48, 48, 48));

        JPanel contenido = Tema.panel(new GridBagLayout());
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.gridx = 0;
        restricciones.gridy = 0;
        restricciones.anchor = GridBagConstraints.CENTER;
        restricciones.insets = new Insets(0, 0, 22, 0);
        contenido.add(new LogoClinica(150), restricciones);

        JLabel nombre = new JLabel("Clínica", SwingConstants.CENTER);
        nombre.setFont(Tema.FUENTE.deriveFont(Font.BOLD, 34f));
        nombre.setForeground(Color.WHITE);
        restricciones.gridy++;
        restricciones.insets = new Insets(0, 0, 10, 0);
        contenido.add(nombre, restricciones);

        JLabel lema = new JLabel("Cuidamos tu salud, cerca de vos", SwingConstants.CENTER);
        lema.setFont(Tema.FUENTE.deriveFont(16f));
        lema.setForeground(new Color(224, 242, 244));
        restricciones.gridy++;
        restricciones.insets = new Insets(0, 0, 0, 0);
        contenido.add(lema, restricciones);

        marca.add(contenido);
        return marca;
    }

    private JPanel crearAcceso() {
        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(Tema.FONDO);
        fondo.setBorder(BorderFactory.createEmptyBorder(36, 48, 36, 48));

        JPanel tarjeta = Tema.tarjeta(null);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setPreferredSize(new Dimension(350, 420));

        JLabel titulo = Tema.titulo("Bienvenido", 28f);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        JLabel subtitulo = Tema.textoSecundario("Ingresá con tu cuenta para continuar.");
        subtitulo.setAlignmentX(LEFT_ALIGNMENT);

        configurarCampo(campoEmail);
        configurarCampo(campoPassword);

        JLabel etiquetaEmail = Tema.etiquetaCampo("Email");
        etiquetaEmail.setLabelFor(campoEmail);
        etiquetaEmail.setAlignmentX(LEFT_ALIGNMENT);
        JLabel etiquetaPassword = Tema.etiquetaCampo("Contraseña");
        etiquetaPassword.setLabelFor(campoPassword);
        etiquetaPassword.setAlignmentX(LEFT_ALIGNMENT);

        JCheckBox mostrarPassword = new JCheckBox("Mostrar contraseña");
        mostrarPassword.setFont(Tema.FUENTE.deriveFont(13f));
        mostrarPassword.setForeground(Tema.TEXTO_SUAVE);
        mostrarPassword.setOpaque(false);
        mostrarPassword.setAlignmentX(LEFT_ALIGNMENT);
        mostrarPassword.addActionListener(evento -> campoPassword.setEchoChar(mostrarPassword.isSelected() ? '\0' : '•'));

        botonIngresar.setAlignmentX(LEFT_ALIGNMENT);
        botonIngresar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        botonIngresar.addActionListener(evento -> ingresar());

        JButton botonRegistro = Tema.botonSecundario("Crear una cuenta");
        botonRegistro.setAlignmentX(LEFT_ALIGNMENT);
        botonRegistro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        botonRegistro.addActionListener(evento -> new Registro(this, icon).setVisible(true));

        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(5));
        tarjeta.add(subtitulo);
        tarjeta.add(Box.createVerticalStrut(28));
        tarjeta.add(etiquetaEmail);
        tarjeta.add(Box.createVerticalStrut(7));
        tarjeta.add(campoEmail);
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(etiquetaPassword);
        tarjeta.add(Box.createVerticalStrut(7));
        tarjeta.add(campoPassword);
        tarjeta.add(Box.createVerticalStrut(7));
        tarjeta.add(mostrarPassword);
        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(botonIngresar);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(botonRegistro);

        fondo.add(tarjeta);
        return fondo;
    }

    private void configurarCampo(javax.swing.JComponent campo) {
        Tema.estilizarCampo(campo);
        campo.setAlignmentX(LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        campo.setPreferredSize(new Dimension(300, 42));
    }

    private void ingresar() {
        String email = campoEmail.getText().trim();
        String password = new String(campoPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            Mensajes.error(this, "Completá el email y la contraseña.");
            return;
        }

        botonIngresar.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            DtUsuario usuario = icon.iniciarSesion(email, password);
            campoPassword.setText("");
            new Principal(icon, usuario).setVisible(true);
            dispose();
        } catch (CredencialesInvalidasException excepcion) {
            campoPassword.setText("");
            campoPassword.requestFocusInWindow();
            Mensajes.error(this, excepcion.getMessage());
        } catch (PersistenciaException excepcion) {
            Mensajes.error(this, excepcion.getMessage());
        } catch (RuntimeException excepcion) {
            excepcion.printStackTrace();
            Mensajes.error(this, "Ocurrió un error inesperado al iniciar sesión.");
        } finally {
            botonIngresar.setEnabled(true);
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private static final class PanelDegradado extends JPanel {

        private static final long serialVersionUID = 1L;

        @Override
        protected void paintComponent(Graphics graficos) {
            Graphics2D g2 = (Graphics2D) graficos.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setPaint(new GradientPaint(0, 0, Tema.AZUL_OSCURO, getWidth(), getHeight(), Tema.AZUL));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    /** Evita repetir la comprobacion de icono al configurar un JFrame. */
    private static final class ImageIconUtil {

        private ImageIconUtil() {
        }

        private static void aplicarIcono(JFrame ventana) {
            javax.swing.ImageIcon icono = LogoClinica.icono(40, 40);
            if (icono != null) {
                ventana.setIconImage(icono.getImage());
            }
        }
    }
}
