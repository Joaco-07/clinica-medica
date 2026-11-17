package publicadores;

import javax.swing.SwingUtilities;

import jakarta.xml.ws.Endpoint;
import presentacion.Login;

public class Servidor {

    private static final String URL = System.getProperty("ws.url", "http://localhost:9876/controlador");

    public static void main(String[] args) {
        Endpoint.publish(URL, new ControladorPublish());

        System.out.println("Servicio publicado. El WSDL esta en:");
        System.out.println("    " + URL + "?wsdl");
        System.out.println("Deja esta ventana abierta: si la cerras, el cliente web deja de funcionar.");

        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
