package logica;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import datatypes.DtOrdenMedica;
import excepciones.CantidadInvalidaException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ordenes_medicas")
public class OrdenMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_email", nullable = false)
    private Paciente paciente;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @OneToMany(
        mappedBy = "ordenMedica",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<LineaOrden> lineas = new ArrayList<>();

    public OrdenMedica() {
    }

    public OrdenMedica(Paciente paciente) {
        this(paciente, LocalDateTime.now());
    }

    public OrdenMedica(Paciente paciente, LocalDateTime fecha) {
        this.paciente = paciente;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public List<LineaOrden> getLineas() {
        return lineas;
    }

    public void agregarLinea(Prestacion prestacion, int cantidad)
            throws CantidadInvalidaException {
        if (cantidad <= 0) {
            throw new CantidadInvalidaException(
                "La cantidad debe ser mayor que cero"
            );
        }

        lineas.add(new LineaOrden(this, prestacion, cantidad));
    }

    public void quitarPrestacion(Long idPrestacion) {
        lineas.removeIf(linea -> linea.getPrestacion().getId().equals(idPrestacion));
    }

    public double getTotal() {
        return lineas.stream()
                .mapToDouble(LineaOrden::getSubtotal)
                .sum();
    }

    public DtOrdenMedica getDtOrdenMedica() {
        return new DtOrdenMedica(
                id,
                fecha,
                lineas.stream()
                        .map(LineaOrden::getDtLineaOrden)
                        .toList()
        );
    }
}
