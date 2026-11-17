package logica;

import java.time.LocalDate;

import datatypes.DtSeguido;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "seguidos",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_seguido_paciente_prestacion",
        columnNames = {"paciente_email", "prestacion_id"}
    )
)
public class Seguido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_email", nullable = false)
    private Paciente paciente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "prestacion_id", nullable = false)
    private Prestacion prestacion;

    @Column(nullable = false)
    private LocalDate fecha;

    public Seguido() {
    }

    public Seguido(Paciente paciente, Prestacion prestacion) {
        this.paciente = paciente;
        this.prestacion = prestacion;
        this.fecha = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Prestacion getPrestacion() {
        return prestacion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public DtSeguido getDtSeguido() {
        return new DtSeguido(
            prestacion.getDtPrestacion(),
            fecha
        );
    }
}