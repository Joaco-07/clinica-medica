package presentacion;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;

/**
 * Colores y estilos compartidos por toda la interfaz.
 *
 * La aplicacion usa solamente Swing. Esta clase permite mantener una apariencia
 * consistente sin agregar bibliotecas externas al proyecto.
 */
public final class Tema {

    public static final Color AZUL = new Color(8, 86, 116);
    public static final Color AZUL_OSCURO = new Color(5, 57, 78);
    public static final Color AZUL_CLARO = new Color(226, 241, 246);
    public static final Color VERDE = new Color(62, 177, 24);
    public static final Color VERDE_OSCURO = new Color(43, 132, 15);
    public static final Color FONDO = new Color(244, 248, 250);
    public static final Color SUPERFICIE = Color.WHITE;
    public static final Color TEXTO = new Color(31, 45, 51);
    public static final Color TEXTO_SUAVE = new Color(94, 111, 119);
    public static final Color BORDE = new Color(209, 222, 228);
    public static final Color ERROR = new Color(183, 48, 48);

    public static final Font FUENTE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FUENTE_NEGRITA = new Font("Segoe UI", Font.BOLD, 14);

    private static boolean instalado;

    private Tema() {
    }

    /** Instala Nimbus, si esta disponible, y configura los valores generales. */
    public static synchronized void instalar() {
        if (instalado) {
            return;
        }

        try {
            for (UIManager.LookAndFeelInfo apariencia : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(apariencia.getName())) {
                    UIManager.setLookAndFeel(apariencia.getClassName());
                    break;
                }
            }
        } catch (Exception excepcion) {
            // Si Nimbus no esta disponible, Swing conserva la apariencia del sistema.
        }

        UIManager.put("defaultFont", FUENTE);
        UIManager.put("control", SUPERFICIE);
        UIManager.put("info", AZUL_CLARO);
        UIManager.put("nimbusBase", AZUL);
        UIManager.put("nimbusBlueGrey", BORDE);
        UIManager.put("nimbusFocus", VERDE);
        UIManager.put("nimbusLightBackground", SUPERFICIE);
        UIManager.put("text", TEXTO);
        UIManager.put("Table.alternateRowColor", new Color(247, 251, 252));
        UIManager.put("ToolTip.font", FUENTE.deriveFont(13f));
        UIManager.put("OptionPane.messageFont", FUENTE);
        UIManager.put("OptionPane.buttonFont", FUENTE_NEGRITA);
        instalado = true;
    }

    public static JPanel panel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    public static JPanel tarjeta(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(SUPERFICIE);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDE, 1, true), BorderFactory.createEmptyBorder(24, 26, 24, 26)));
        return panel;
    }

    public static JLabel titulo(String texto, float tamano) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(FUENTE.deriveFont(Font.BOLD, tamano));
        etiqueta.setForeground(TEXTO);
        return etiqueta;
    }

    public static JLabel textoSecundario(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(FUENTE);
        etiqueta.setForeground(TEXTO_SUAVE);
        return etiqueta;
    }

    public static JLabel etiquetaCampo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(FUENTE_NEGRITA);
        etiqueta.setForeground(TEXTO);
        etiqueta.setVerticalAlignment(SwingConstants.CENTER);
        return etiqueta;
    }

    public static JButton botonPrimario(String texto) {
        JButton boton = botonBase(texto);
        boton.setBackground(AZUL);
        boton.setForeground(Color.WHITE);
        boton.setBorder(bordeBoton(AZUL));
        return boton;
    }

    public static JButton botonSecundario(String texto) {
        JButton boton = botonBase(texto);
        boton.setBackground(SUPERFICIE);
        boton.setForeground(AZUL_OSCURO);
        boton.setBorder(bordeBoton(BORDE));
        return boton;
    }

    public static JButton botonPeligro(String texto) {
        JButton boton = botonBase(texto);
        boton.setBackground(ERROR);
        boton.setForeground(Color.WHITE);
        boton.setBorder(bordeBoton(ERROR));
        return boton;
    }

    /** Elige un estilo razonable a partir de la accion que expresa el texto. */
    public static JButton botonAccion(String texto) {
        String normalizado = texto.toLowerCase();
        if (normalizado.contains("eliminar") || normalizado.contains("quitar")) {
            return botonPeligro(texto);
        }
        if (normalizado.contains("cerrar") || normalizado.contains("cancelar") || normalizado.contains("actualizar")) {
            return botonSecundario(texto);
        }
        return botonPrimario(texto);
    }

    private static JButton botonBase(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(FUENTE_NEGRITA);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setMargin(new Insets(9, 18, 9, 18));
        boton.setOpaque(true);
        return boton;
    }

    private static Border bordeBoton(Color color) {
        return BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(color, 1, true), BorderFactory.createEmptyBorder(7, 15, 7, 15));
    }

    /** Aplica el mismo borde, fuente y altura visual a campos y selectores. */
    public static void estilizarCampo(Component campo) {
        campo.setFont(FUENTE);
        if (campo instanceof JComponent componente) {
            componente.setForeground(TEXTO);
            componente.setBackground(SUPERFICIE);
            componente.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDE, 1, true), BorderFactory.createEmptyBorder(7, 9, 7, 9)));
        }
        if (campo instanceof JTextComponent texto) {
            texto.setCaretColor(AZUL_OSCURO);
            texto.setSelectionColor(AZUL_CLARO);
            texto.setSelectedTextColor(TEXTO);
        }
    }

    public static void estilizarTabla(JTable tabla) {
        tabla.setFont(FUENTE);
        tabla.setForeground(TEXTO);
        tabla.setBackground(SUPERFICIE);
        tabla.setGridColor(new Color(229, 237, 240));
        tabla.setRowHeight(32);
        tabla.setShowVerticalLines(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setSelectionBackground(AZUL_CLARO);
        tabla.setSelectionForeground(TEXTO);

        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setFont(FUENTE_NEGRITA);
        encabezado.setForeground(AZUL_OSCURO);
        encabezado.setBackground(new Color(235, 245, 248));
        encabezado.setReorderingAllowed(false);
        encabezado.setPreferredSize(new java.awt.Dimension(encabezado.getPreferredSize().width, 36));
    }
}
