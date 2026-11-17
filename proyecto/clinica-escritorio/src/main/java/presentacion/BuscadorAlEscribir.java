package presentacion;

import java.util.Objects;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Ejecuta la misma accion cada vez que se modifica el texto de un campo. */
public final class BuscadorAlEscribir implements DocumentListener {

    private final Runnable accion;

    public BuscadorAlEscribir(Runnable accion) {
        this.accion = Objects.requireNonNull(accion);
    }

    @Override
    public void insertUpdate(DocumentEvent evento) {
        accion.run();
    }

    @Override
    public void removeUpdate(DocumentEvent evento) {
        accion.run();
    }

    @Override
    public void changedUpdate(DocumentEvent evento) {
        accion.run();
    }
}
