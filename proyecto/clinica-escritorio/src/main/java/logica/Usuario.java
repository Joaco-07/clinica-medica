package logica;

import datatypes.DtUsuario;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")


public abstract class Usuario {
    @Id
    private  String email;
    private String nombre;
    private String password;

    //Constructor vacio
    public Usuario(){}      

     //Constructor
    public Usuario(String email, String nombre, String password) {
        this.email = email;
        this.nombre = nombre;
        this.password = password;
    }
    
    public abstract DtUsuario getDtUsuario();

    public String getEmail() { return email; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

}