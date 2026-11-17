package logica;

import datatypes.DtLineaOrden;
import excepciones.CantidadInvalidaException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "lineas_orden")
public class LineaOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "orden_medica_id", nullable = false)
    private OrdenMedica ordenMedica;

    @ManyToOne(optional = false)
    @JoinColumn(name = "prestacion_id", nullable = false)
    private Prestacion prestacion;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false)
    private double precioUnitario;

    public LineaOrden() {
    }

    public LineaOrden(
            OrdenMedica ordenMedica,
            Prestacion prestacion,
            int cantidad) throws CantidadInvalidaException {

        if (cantidad <= 0) {
            throw new CantidadInvalidaException(
                "La cantidad debe ser mayor que cero"
            );
        }

        this.ordenMedica = ordenMedica;
        this.prestacion = prestacion;
        this.cantidad = cantidad;
        this.precioUnitario = prestacion.getPrecio();
    }

    public Long getId() {
        return id;
    }

    public OrdenMedica getOrdenMedica() {
        return ordenMedica;
    }

    public Prestacion getPrestacion() {
        return prestacion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public double getSubtotal() {
        return precioUnitario * cantidad;
    }

    public DtLineaOrden getDtLineaOrden() {
        return new DtLineaOrden(
                prestacion.getNombre(),
                cantidad,
                precioUnitario
        );
    }
}
