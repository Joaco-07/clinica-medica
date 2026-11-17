package presentacion;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import excepciones.PersistenciaException;
import excepciones.UsuarioRepetidoException;
import interfaces.IControlador;

/** Formulario de registro para pacientes y medicos. */
public final class Registro extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final String PACIENTE = "Paciente";
    private static final String MEDICO = "Médico";

    private final IControlador icon;
    private final JTextField campoEmail = new JTextField(22);
    private final JTextField campoNombre = new JTextField(22);
    private final JPasswordField campoPassword = new JPasswordField(22);
    private final JPasswordField campoConfirmacion = new JPasswordField(22);
    private final JComboBox<String> campoTipo = new JComboBox<>(new String[] { PACIENTE, MEDICO });
    private final JTextField campoExtra = new JTextField(22);
    private final JLabel etiquetaExtra = Tema.etiquetaCampo("Mutualista");

    public Registro(JFrame padre, IControlador icon) {
        super(padre, "Crear una cuenta", true);
        this.icon = icon;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(590, 670);
        setMinimumSize(new Dimension(540, 620));
        setLocationRelativeTo(padre);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Tema.FONDO);
        setContentPane(raiz);
        raiz.add(crearEncabezado(), BorderLayout.NORTH);
        raiz.add(crearFormulario(), BorderLayout.CENTER);
        raiz.add(crearBotones(), BorderLayout.SOUTH);

        campoTipo.addActionListener(evento -> actualizarTipo());
        actualizarTipo();
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout(16, 0));
        encabezado.setBackground(Tema.SUPERFICIE);
        encabezado.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.BORDE), BorderFactory.createEmptyBorder(16, 28, 16, 24)));

        JPanel textos = Tema.panel(new BorderLayout(0, 4));
        textos.add(Tema.titulo("Crear una cuenta", 24f), BorderLayout.CENTER);
        textos.add(Tema.textoSecundario("Completá tus datos para registrarte en la clínica."), BorderLayout.SOUTH);
        encabezado.add(textos, BorderLayout.CENTER);
        encabezado.add(new LogoClinica(68), BorderLayout.EAST);
        return encabezado;
    }

    private JPanel crearFormulario() {
        JPanel exterior = Tema.panel(new BorderLayout());
        exterior.setBorder(BorderFactory.createEmptyBorder(22, 34, 16, 34));

        JPanel formulario = Tema.tarjeta(new GridBagLayout());
        agregarFila(formulario, 0, "Tipo de usuario", campoTipo);
        agregarFila(formulario, 1, "Email", campoEmail);
        agregarFila(formulario, 2, "Nombre completo", campoNombre);
        agregarFila(formulario, 3, "Contraseña", campoPassword);
        agregarFila(formulario, 4, "Repetir contraseña", campoConfirmacion);
        agregarFila(formulario, 5, etiquetaExtra, campoExtra);
        exterior.add(formulario, BorderLayout.CENTER);
        return exterior;
    }

    private void agregarFila(JPanel formulario, int fila, String etiqueta, java.awt.Component campo) {
        agregarFila(formulario, fila, Tema.etiquetaCampo(etiqueta), campo);
    }

    private void agregarFila(JPanel formulario, int fila, JLabel etiqueta, java.awt.Component campo) {
        Tema.estilizarCampo(campo);
        etiqueta.setLabelFor(campo);

        GridBagConstraints izquierda = new GridBagConstraints();
        izquierda.gridx = 0;
        izquierda.gridy = fila;
        izquierda.anchor = GridBagConstraints.LINE_START;
        izquierda.insets = new Insets(0, 0, 13, 18);
        formulario.add(etiqueta, izquierda);

        GridBagConstraints derecha = new GridBagConstraints();
        derecha.gridx = 1;
        derecha.gridy = fila;
        derecha.weightx = 1;
        derecha.fill = GridBagConstraints.HORIZONTAL;
        derecha.insets = new Insets(0, 0, 13, 0);
        formulario.add(campo, derecha);
    }

    private JPanel crearBotones() {
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setBackground(Tema.FONDO);
        botones.setBorder(BorderFactory.createEmptyBorder(0, 34, 22, 34));

        JButton cancelar = Tema.botonSecundario("Cancelar");
        cancelar.addActionListener(evento -> dispose());
        JButton registrar = Tema.botonPrimario("Crear cuenta");
        registrar.addActionListener(evento -> registrar());
        botones.add(cancelar);
        botones.add(registrar);
        getRootPane().setDefaultButton(registrar);
        return botones;
    }

    private void actualizarTipo() {
        if (esMedico()) {
            etiquetaExtra.setText("Especialidad");
            campoExtra.setToolTipText("Especialidad profesional del médico");
        } else {
            etiquetaExtra.setText("Mutualista");
            campoExtra.setToolTipText("Mutualista del paciente");
        }
    }

    private boolean esMedico() {
        return MEDICO.equals(campoTipo.getSelectedItem());
    }

    private void registrar() {
        String email = campoEmail.getText().trim();
        String nombre = campoNombre.getText().trim();
        String password = new String(campoPassword.getPassword());
        String confirmacion = new String(campoConfirmacion.getPassword());
        String extra = campoExtra.getText().trim();

        if (email.isEmpty() || nombre.isEmpty() || password.isEmpty() || confirmacion.isEmpty() || extra.isEmpty()) {
            Mensajes.error(this, "Completá todos los campos.");
            return;
        }
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            Mensajes.error(this, "Ingresá un email válido.");
            campoEmail.requestFocusInWindow();
            return;
        }
        if (!password.equals(confirmacion)) {
            Mensajes.error(this, "Las contraseñas no coinciden.");
            campoPassword.setText("");
            campoConfirmacion.setText("");
            campoPassword.requestFocusInWindow();
            return;
        }

        try {
            if (esMedico()) {
                icon.registrarMedico(email, nombre, password, extra);
            } else {
                icon.registrarPaciente(email, nombre, password, extra);
            }
            Mensajes.info(this, "Tu cuenta fue creada correctamente. Ya podés ingresar.");
            dispose();
        } catch (UsuarioRepetidoException excepcion) {
            Mensajes.error(this, excepcion.getMessage());
        } catch (PersistenciaException excepcion) {
            Mensajes.error(this, excepcion.getMessage());
        } catch (RuntimeException excepcion) {
            excepcion.printStackTrace();
            Mensajes.error(this, "Ocurrió un error inesperado al registrar el usuario.");
        }
    }
}
