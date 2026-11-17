package publicadores;

import java.util.ArrayList;
import java.util.List;

public class OrdenWS {

    private Long id;
    private String fecha;
    private double total;
    private List<LineaOrdenWS> lineas = new ArrayList<>();

    public OrdenWS() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<LineaOrdenWS> getLineas() {
        return lineas;
    }

    public void setLineas(List<LineaOrdenWS> lineas) {
        this.lineas = lineas;
    }
}