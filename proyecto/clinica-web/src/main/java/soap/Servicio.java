package soap;

import java.net.MalformedURLException;
import java.net.URL;

import jakarta.xml.ws.BindingProvider;
import publicadores.ControladorPublish;
import publicadores.ControladorPublishService;

/**
 * Centraliza el acceso al servicio SOAP de la clínica.
 *
 * La aplicación web utiliza esta clase para comunicarse con el
 * servidor SOAP publicado desde clinica-escritorio.
 *
 * Las clases del paquete publicadores son generadas por Maven
 * a partir del WSDL del servidor.
 */
public class Servicio {

    /**
     * Dirección del servicio SOAP.
     *
     * Se puede cambiar al arrancar Tomcat mediante:
     * -Dws.url=http://localhost:9876/controlador
     */
    private static final String URL_SERVICIO =
        System.getProperty(
                "ws.url",
                "http://host.docker.internal:9876/controlador"
        );

    /**
     * Servicio SOAP generado a partir del WSDL.
     */
    private static ControladorPublishService servicio;

    private Servicio() {
    }

    /**
     * Obtiene la instancia del servicio SOAP.
     *
     * Se crea una sola vez para evitar descargar y analizar
     * el WSDL cada vez que se necesita una operación.
     */
    private static synchronized ControladorPublishService getServicio() {
        if (servicio == null) {
            try {
                servicio = new ControladorPublishService(
                        new URL(URL_SERVICIO + "?wsdl")
                );
            } catch (MalformedURLException e) {
                throw new IllegalStateException(
                        "La dirección del servicio SOAP no es válida: "
                                + URL_SERVICIO,
                        e
                );
            }
        }

        return servicio;
    }

    /**
     * Obtiene el puerto SOAP para invocar las operaciones
     * publicadas por el controlador de la clínica.
     */
    public static ControladorPublish getPort() {
        ControladorPublish port =
                getServicio().getControladorPublishPort();

        // Establece la dirección a la que se enviarán las peticiones SOAP.
        ((BindingProvider) port).getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                URL_SERVICIO
        );

        return port;
    }
}