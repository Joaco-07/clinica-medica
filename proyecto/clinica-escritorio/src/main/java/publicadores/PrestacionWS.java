package publicadores;

import datatypes.Franja;

public class PrestacionWS {

    private Long id;
    private String nombre;
    private double precio;
    private Franja franja;
    private TipoPrestacion tipo;

    // Propio de ESTUDIO
    private Integer duracionMinutos;

    // Propios de TERAPIA
    private boolean requiereDerivacion;
    private Integer cantidadSesiones;

    public PrestacionWS() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Franja getFranja() {
        return franja;
    }

    public void setFranja(Franja franja) {
        this.franja = franja;
    }

    public TipoPrestacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoPrestacion tipo) {
        this.tipo = tipo;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public boolean isRequiereDerivacion() {
        return requiereDerivacion;
    }

    public void setRequiereDerivacion(boolean requiereDerivacion) {
        this.requiereDerivacion = requiereDerivacion;
    }

    public Integer getCantidadSesiones() {
        return cantidadSesiones;
    }

    public void setCantidadSesiones(Integer cantidadSesiones) {
        this.cantidadSesiones = cantidadSesiones;
    }
}