package presentacion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/** Logo reutilizable de la clinica, cargado desde los recursos de la aplicacion. */
public final class LogoClinica extends JLabel {

    private static final long serialVersionUID = 1L;
    private static final String RUTA = "/imagenes/logo-clinica.png";
    private static final BufferedImage IMAGEN_ORIGINAL = cargarImagen();

    public LogoClinica(int tamano) {
        this(tamano, tamano);
    }

    public LogoClinica(int ancho, int alto) {
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setOpaque(false);
        setIcon(icono(ancho, alto));
        setToolTipText("Clínica");
        getAccessibleContext().setAccessibleName("Logo de la clínica");

        if (getIcon() == null) {
            setText("+");
            setFont(new Font("Segoe UI", Font.BOLD, Math.max(24, alto / 2)));
            setForeground(new Color(62, 177, 24));
        }
    }

    /** Crea un icono nuevo conservando la proporcion original. */
    public static ImageIcon icono(int anchoMaximo, int altoMaximo) {
        if (IMAGEN_ORIGINAL == null || anchoMaximo <= 0 || altoMaximo <= 0) {
            return null;
        }

        double escala = Math.min((double) anchoMaximo / IMAGEN_ORIGINAL.getWidth(), (double) altoMaximo / IMAGEN_ORIGINAL.getHeight());
        int ancho = Math.max(1, (int) Math.round(IMAGEN_ORIGINAL.getWidth() * escala));
        int alto = Math.max(1, (int) Math.round(IMAGEN_ORIGINAL.getHeight() * escala));
        Image escalada = IMAGEN_ORIGINAL.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(escalada);
    }

    private static BufferedImage cargarImagen() {
        URL recurso = LogoClinica.class.getResource(RUTA);
        if (recurso == null) {
            return null;
        }

        try {
            return ImageIO.read(recurso);
        } catch (IOException excepcion) {
            return null;
        }
    }
}
