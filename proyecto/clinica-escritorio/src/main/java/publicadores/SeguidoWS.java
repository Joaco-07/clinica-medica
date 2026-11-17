package publicadores;

public class SeguidoWS {

    private PrestacionWS prestacion;
    private String fecha;

    public SeguidoWS() {
    }

    public PrestacionWS getPrestacion() {
        return prestacion;
    }

    public void setPrestacion(PrestacionWS prestacion) {
        this.prestacion = prestacion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}