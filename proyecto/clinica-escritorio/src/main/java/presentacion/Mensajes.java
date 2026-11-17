package presentacion;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JOptionPane;

/** Centraliza los mensajes para mantener el mismo estilo en todas las ventanas. */
public final class Mensajes {

    private Mensajes() {
    }

    public static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Clínica", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void info(Component padre, String mensaje, Icon imagen) {
        JOptionPane.showMessageDialog(padre, mensaje, "Clínica", JOptionPane.INFORMATION_MESSAGE, imagen);
    }

    public static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "No se pudo completar la operación", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirmar(Component padre, String mensaje) {
        return JOptionPane.showConfirmDialog(padre, mensaje, "Confirmar acción", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }
}
