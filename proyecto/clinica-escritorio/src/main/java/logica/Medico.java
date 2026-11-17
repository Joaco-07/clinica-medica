package logica;

import datatypes.DtMedico;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MEDICO")
public class Medico extends Usuario {

    private String especialidad;

    public Medico() {   //Constructor vacio
    }

    public Medico(String email, String nombre, String password, String especialidad) {      //Constructor
        super(email, nombre, password);
        this.especialidad = especialidad;
    }

    public String getEspecialidad() { return especialidad; } //Getter especialidad

    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; } //Setter especialidad

    @Override
    public DtMedico getDtUsuario() {
        return new DtMedico(getEmail(), getNombre(), especialidad);
    }
}

