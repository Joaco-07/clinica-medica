
package publicadores;

public class LineaSolicitudWS {

    private Long idPrestacion;
    private int cantidad;

    public LineaSolicitudWS() {
    }

    public Long getIdPrestacion() {
        return idPrestacion;
    }

    public void setIdPrestacion(Long idPrestacion) {
        this.idPrestacion = idPrestacion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
