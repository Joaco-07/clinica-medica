package soporte;

/** Una prestación seleccionada en la solicitud temporal de la sesión web. */
public class ItemSolicitud {

    private final Long idPrestacion;
    private final String nombre;
    private final double precio;
    private int cantidad;

    public ItemSolicitud(Long idPrestacion, String nombre, double precio, int cantidad) {
        this.idPrestacion = idPrestacion;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public Long getIdPrestacion() { return idPrestacion; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public int getCantidad() { return cantidad; }
    public double getSubtotal() { return precio * cantidad; }

    public void sumar(int delta) {
        cantidad = Math.addExact(cantidad, delta);
    }
}
